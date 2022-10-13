/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (c) 2022. Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.lista

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Extension of [androidx.recyclerview.widget.AsyncListDiffer]
 * that supports submitting lists immediately without applying diffing.
 */
class ListaAsyncDiffer<T>(
    private val updateCallback: UpdateCallback,
    private val config: Config<T>
) {

    private val backgroundExecutor = config.backgroundThreadExecutor
    private val mainExecutor = config.mainThreadExecutor
    private val listeners = CopyOnWriteArrayList<AsyncListDiffer.ListListener<T>>()
    private var list: List<T?>? = null

    // Max generation of currently scheduled runnable
    private var maxScheduledGeneration: Int = 0

    /**
     * Get the current List - any diffing to present this list has already been computed and
     * dispatched via the ListUpdateCallback.
     *
     * If a `null` List, or no List has been submitted, an empty list will be returned.
     *
     * The returned list may not be mutated - mutations to content must be done through
     * [submitList]
     *
     * @return current List.
     */
    @MainThread
    fun getCurrentList(): List<T?> {
        if (list == null) {
            return Collections.emptyList()
        }
        return list!!
    }

    @MainThread
    fun submitNow(newList: List<T?>) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        maxScheduledGeneration++
        if (newList === list) {
            return
        }
        val oldList = getCurrentList()
        list = newList
        maxScheduledGeneration++
        updateCallback.onInvalidated()
        onCurrentListChanged(oldList)
    }

    /**
     * Pass a new List to the AdapterHelper. Adapter updates will be computed on a background
     * thread.
     *
     *
     * If a List is already present, a diff will be computed asynchronously on a background thread.
     * When the diff is computed, it will be applied (dispatched to the [ListUpdateCallback]),
     * and the new List will be swapped in.
     *
     *
     * The commit callback can be used to know when the List is committed, but note that it
     * may not be executed. If List B is submitted immediately after List A, and is
     * committed directly, the callback associated with List A will not be run.
     *
     * @param newList The new List.
     * @param commitCallback Optional runnable that is executed when the List is committed, if
     * it is committed.
     */
    @MainThread
    fun submitList(
        newList: List<T?>?,
        commitCallback: Runnable? = null
    ) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = ++maxScheduledGeneration

        if (newList === list) {
            // nothing to do (Note - still had to inc generation, since may have ongoing work)
            commitCallback?.run()
            return
        }

        val previousList = getCurrentList()

        // fast simple remove all
        if (newList == null) {
            val countRemoved = list!!.size
            list = null
            // notify last, after list is updated
            updateCallback.onRemoved(0, countRemoved)
            onCurrentListChanged(previousList, commitCallback)
            return
        }

        // fast simple first insert
        if (list == null) {
            list = newList
            // notify last, after list is updated
            updateCallback.onInserted(0, newList.size)
            onCurrentListChanged(previousList, commitCallback)
            return
        }

        val oldList = list
        backgroundExecutor.execute {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldList!!.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldList!![oldItemPosition]
                    val newItem = newList[newItemPosition]
                    return if (oldItem != null && newItem != null) {
                        config.diffCallback.areItemsTheSame(oldItem, newItem)
                    } else oldItem == null && newItem == null
                    // If both items are null we consider them the same.
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldItem = oldList!![oldItemPosition]
                    val newItem = newList[newItemPosition]
                    if (oldItem != null && newItem != null) {
                        return config.diffCallback.areContentsTheSame(oldItem, newItem)
                    }
                    if (oldItem == null && newItem == null) {
                        return true
                    }
                    // There is an implementation bug if we reach this point. Per the docs, this
                    // method should only be invoked when areItemsTheSame returns true. That
                    // only occurs when both items are non-null or both are null and both of
                    // those cases are handled above.
                    throw AssertionError()
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    val oldItem = oldList!![oldItemPosition]
                    val newItem = newList[newItemPosition]
                    if (oldItem != null && newItem != null) {
                        return config.diffCallback.getChangePayload(oldItem, newItem)
                    }
                    // There is an implementation bug if we reach this point. Per the docs, this
                    // method should only be invoked when areItemsTheSame returns true AND
                    // areContentsTheSame returns false. That only occurs when both items are
                    // non-null which is the only case handled above.
                    throw AssertionError()
                }
            })

            mainExecutor.execute {
                if (maxScheduledGeneration == runGeneration) {
                    latchList(newList, result, commitCallback)
                }
            }
        }
    }

    /**
     * Add a ListListener to receive updates when the current List changes.
     *
     * @param listener Listener to receive updates.
     *
     * @see getCurrentList
     * @see removeListListener
     */
    fun addListListener(listener: AsyncListDiffer.ListListener<T>) {
        listeners.add(listener)
    }

    /**
     * Remove a previously registered ListListener.
     *
     * @param listener Previously registered listener.
     * @see getCurrentList
     * @see addListListener
     */
    fun removeListListener(listener: AsyncListDiffer.ListListener<T>) {
        listeners.remove(listener)
    }

    fun clearListListeners() {
        listeners.clear()
    }

    private fun latchList(
        newList: List<T?>,
        diffResult: DiffUtil.DiffResult,
        commitCallback: Runnable?
    ) {
        val previousList = getCurrentList()
        list = newList
        diffResult.dispatchUpdatesTo(updateCallback)
        onCurrentListChanged(previousList, commitCallback)
    }

    private fun onCurrentListChanged(previousList: List<T?>, commitCallback: Runnable? = null) {
        val currentList = getCurrentList()
        for (listener in listeners) {
            listener.onCurrentListChanged(previousList, currentList)
        }
        commitCallback?.run()
    }

    class Config<T> private constructor(
        val diffCallback: DiffUtil.ItemCallback<T>,
        val mainThreadExecutor: Executor,
        val backgroundThreadExecutor: Executor
    ) {

        companion object {

            private val mainThreadExecutor: MainThreadExecutor by lazy { MainThreadExecutor() }
            private var defaultBackgroundExecutor: Executor? = null
            private val defaultExecutorLock = Any()

            @JvmStatic
            fun <T> build(diffCallback: DiffUtil.ItemCallback<T>): Config<T> {
                return Config(diffCallback, mainThreadExecutor, getOrCreateBackgroundExecutor())
            }

            @JvmStatic
            fun <T> build(
                diffCallback: DiffUtil.ItemCallback<T>,
                backgroundExecutor: Executor
            ): Config<T> {
                return Config(diffCallback, mainThreadExecutor, backgroundExecutor)
            }

            @VisibleForTesting
            fun <T> buildTest(
                diffCallback: DiffUtil.ItemCallback<T>,
                mainThreadExecutor: Executor,
                backgroundExecutor: Executor = getOrCreateBackgroundExecutor()
            ): Config<T> {
                return Config(diffCallback, mainThreadExecutor, backgroundExecutor)
            }

            private fun getOrCreateBackgroundExecutor(): Executor {
                val backgroundExecutor =
                    defaultBackgroundExecutor ?: synchronized(defaultExecutorLock) {
                        val newExecutor = Executors.newFixedThreadPool(4)
                        defaultBackgroundExecutor = newExecutor
                        newExecutor
                    }
                return backgroundExecutor
            }

        }

        private class MainThreadExecutor : Executor {
            private val handler = Handler(Looper.getMainLooper())
            override fun execute(command: Runnable) {
                handler.post(command)
            }
        }

    }


    interface UpdateCallback : ListUpdateCallback {

        /**
         * Called when the entire adapter contents should be invalidated.
         * This should trigger [RecyclerView.Adapter.notifyDataSetChanged]
         */
        fun onInvalidated()
    }

}
