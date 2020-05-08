/*
 * Copyright (c) 2020. Cabriole
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

package io.cabriole.lista

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.*

/**
 * A controller for a RecyclerView that displays a list of items of type [T].
 *
 * Adapter changes are only dispatched when it's safe to do so
 */
abstract class ListaController<T : Any>(
    private val lifecycle: Lifecycle
) : LifecycleObserver, AsyncListDiffer.ListListener<T> {

    /**
     * Items that need to be dispatched to the RecyclerView adapter
     */
    private var pendingItems: List<T>? = null
    private var pendingDiffing = false
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = Runnable {
        pendingItems?.let {
            submitList(it, false, pendingDiffing)
        }
    }
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var recyclerView: RecyclerView? = null

    /**
     * The adapter that'll be bound to the [RecyclerView]
     *
     * This will only be available after a call to [setup]
     */
    private lateinit var adapter: ListaAdapter<T>

    /**
     *  ScrollListener that dispatches [pendingItems] when the RecyclerView stops scrolling
     *
     *  This ScrollListener is only used if [supportsDispatchingItemsDuringScroll] returns false
     */
    private val itemDispatchScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (pendingItems != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
                submitList(pendingItems!!, false, pendingDiffing)
            }
        }
    }

    abstract fun addSections(adapter: ListaAdapter<T>, recyclerView: RecyclerView)

    abstract fun createDiffItemCallback(): DiffUtil.ItemCallback<T>

    open fun createAdapter(): ListaAdapter<T> {
        return ListaAdapter(createDiffItemCallback())
    }

    /**
     * @return the layout manager to be used by the RecyclerView bound in [setup]
     */
    open fun createLayoutManager(context: Context): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    /**
     * @return the item decorations to be used by the RecyclerView bound in [setup]
     */
    open fun createItemDecorations(layoutManager: RecyclerView.LayoutManager)
            : List<RecyclerView.ItemDecoration> {
        return emptyList()
    }

    /**
     * @return the item animator to be used by the RecyclerView bound in [setup]
     */
    open fun createItemAnimator(): RecyclerView.ItemAnimator? {
        return DefaultItemAnimator()
    }

    /**
     * @return a custom [RecyclerView.RecycledViewPool] or null if the default one should be used
     */
    open fun createRecycledViewPool(): RecyclerView.RecycledViewPool? {
        return null
    }

    /**
     * If the RecyclerView always fits the whole width/height, this should be true
     */
    open fun hasFixedSize() = true

    /**
     * This will be called when the adapter is changed from empty to non-empty
     */
    open fun animateRecyclerView(recyclerView: RecyclerView) {
        recyclerView.alpha = 0.0f
        recyclerView.animate().alpha(1.0f).duration = 350
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onViewDestroyed() {
        val lm = layoutManager

        // This makes sure all children will dispatch onDetachedToWindow to their ViewHolders
        if (lm is LinearLayoutManager) {
            lm.recycleChildrenOnDetach = true
        }

        adapter.clearOnListChangedListeners()
        recyclerView?.layoutManager = null
        recyclerView = null
    }

    fun isEmpty() = adapter.itemCount == 0

    fun getAdapter() = adapter

    open fun setup(recyclerView: RecyclerView) {
        // Check if we already setup a RecyclerView
        if (this.recyclerView != null) {
            return
        }

        // We want to make sure all ViewHolders get a call to onDetachedFromWindow
        // when the lifecycle is destroyed
        lifecycle.addObserver(this)

        adapter = createAdapter()
        adapter.addOnListChangedListener(this)

        // Set the default LayoutManager
        layoutManager = createLayoutManager(recyclerView.context)
        recyclerView.layoutManager = layoutManager

        val recycledViewPool = createRecycledViewPool()
        if (recycledViewPool != null) {
            recyclerView.setRecycledViewPool(recycledViewPool)
        }

        // Add the delegates for this adapter.
        // The LayoutManager needs to be set before, since some sections might need access to it
        addSections(adapter, recyclerView)

        // If the RecyclerView always fits the whole width/height, this should be true
        recyclerView.setHasFixedSize(hasFixedSize())

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = createItemAnimator()

        // Apply default item decorations
        val itemDecorations = createItemDecorations(layoutManager!!)
        itemDecorations.forEach {
            recyclerView.addItemDecoration(it)
        }

        // Add a scroll listener that'll apply pending items when this RecyclerView is idle
        if (!supportsDispatchingItemsDuringScroll()) {
            recyclerView.addOnScrollListener(itemDispatchScrollListener)
        }

        this.recyclerView = recyclerView
    }

    /**
     * When [dispatchImmediately] = false, this class will check for [isDispatchingAdapterChangesSafe].
     *
     * If the RecyclerView is still computing layout, scrolling or animating,
     * the new items won't be dispatched immediately in that case.
     *
     * When [applyDiffing] is set to false, [ListaAdapter.notifyDataSetChanged] is called
     * instead of performing individual updates like [ListaAdapter.notifyItemChanged]
     */
    open fun submitList(
        items: List<T>,
        dispatchImmediately: Boolean = false,
        applyDiffing: Boolean = true
    ) {
        val currentRecyclerView = recyclerView
            ?: throw IllegalStateException(
                "A RecyclerView wasn't found. Setup must be called before"
            )

        if (dispatchImmediately || isDispatchingAdapterChangesSafe(currentRecyclerView)) {
            // Cancel any pending checks or updates
            currentRecyclerView.itemAnimator?.isRunning(null)
            updateHandler.removeCallbacks(updateRunnable)
            // Insert the data into the adapter
            adapter.submitList(items, applyDiffing)
            // Clear any pending items
            pendingItems = null
            pendingDiffing = false
        } else {
            pendingItems = items
            pendingDiffing = applyDiffing
            // If the RecyclerView is computing layout, delay the change
            if (currentRecyclerView.isComputingLayout) {
                updateHandler.post(updateRunnable)
            } else if (currentRecyclerView.isAnimating) {
                // If the RecyclerView is still animating, wait until animations are finished
                currentRecyclerView.itemAnimator?.isRunning {
                    currentRecyclerView.itemAnimator?.isRunning(null)
                    updateHandler.post(updateRunnable)
                }
            } // Else, we're scrolling
        }
    }

    override fun onCurrentListChanged(previousList: MutableList<T>, currentList: MutableList<T>) {
        // Invalidate item decorations after adapter changes
        recyclerView?.invalidateItemDecorations()

        if (previousList.isEmpty() && currentList.isNotEmpty()) {
            recyclerView?.let { animateRecyclerView(it) }
        }
    }

    open fun supportsDispatchingItemsDuringScroll(): Boolean {
        return true
    }

    private fun isDispatchingAdapterChangesSafe(recyclerView: RecyclerView): Boolean {
        if (supportsDispatchingItemsDuringScroll()) {
            return !recyclerView.isComputingLayout && !recyclerView.isAnimating
        }
        return recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE
                && !recyclerView.isComputingLayout
                && !recyclerView.isAnimating
    }

}
