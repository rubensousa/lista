/*
 * Copyright 2022 Rúben Sousa
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
import androidx.recyclerview.widget.RecyclerView

/**
 * Updates the Adapter of a RecyclerView when it's safe to do so.
 *
 * If the RecyclerView is still computing layout, scrolling or animating,
 * the new items won't be dispatched immediately in that case.
 */
class ListaUpdateDispatcher<T> {

    private var isDispatchDuringScrollEnabled = true
    private var pendingUpdate: ListUpdate<T>? = null
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = Runnable {
        pendingUpdate?.let { dispatchUpdate(it) }
    }

    /**
     *  ScrollListener that dispatches [pendingUpdate] when the RecyclerView stops scrolling
     *
     *  This ScrollListener is only used if [dispatchDuringScroll] is false
     */
    private val itemDispatchScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                pendingUpdate?.let { dispatchUpdate(it) }
            }
        }
    }

    fun setup(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(itemDispatchScrollListener)
    }

    fun destroy(recyclerView: RecyclerView) {
        // Cancel any pending adapter update
        cancelPendingUpdate(recyclerView)
        recyclerView.removeOnScrollListener(itemDispatchScrollListener)
    }

    fun update(
        recyclerView: RecyclerView,
        adapter: ListaAdapter<T>,
        items: List<T>,
        applyDiffing: Boolean,
        dispatchDuringScroll: Boolean
    ) {
        isDispatchDuringScrollEnabled = dispatchDuringScroll
        // Cancel any pending update immediately
        cancelPendingUpdate(recyclerView)

        // If we can dispatch the items, do it now
        if (isDispatchingAdapterChangesSafe(recyclerView)) {
            if (applyDiffing) {
                adapter.submit(items)
            } else {
                adapter.submitNow(items)
            }
            return
        }

        pendingUpdate = ListUpdate(recyclerView, adapter, items, applyDiffing)
        if (recyclerView.isComputingLayout) {
            updateHandler.post(updateRunnable)
        } else if (recyclerView.isAnimating) {
            recyclerView.itemAnimator?.isRunning {
                recyclerView.itemAnimator?.isRunning(null)
                updateHandler.post(updateRunnable)
            }
        }
    }

    private fun cancelPendingUpdate(recyclerView: RecyclerView) {
        if (pendingUpdate == null) {
            return
        }
        // Cancel any pending checks or updates for the animations
        recyclerView.itemAnimator?.isRunning(null)
        pendingUpdate = null
        updateHandler.removeCallbacks(updateRunnable)
    }

    private fun dispatchUpdate(update: ListUpdate<T>) {
        update(
            update.recyclerView,
            update.adapter,
            update.items,
            update.applyDiffing,
            isDispatchDuringScrollEnabled
        )
    }

    private fun isDispatchingAdapterChangesSafe(recyclerView: RecyclerView): Boolean {
        if (isDispatchDuringScrollEnabled) {
            return !recyclerView.isComputingLayout && !recyclerView.isAnimating
        }
        return recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE
                && !recyclerView.isComputingLayout
                && !recyclerView.isAnimating
    }

    data class ListUpdate<T>(
        val recyclerView: RecyclerView,
        val adapter: ListaAdapter<T>,
        val items: List<T>,
        val applyDiffing: Boolean
    )

}
