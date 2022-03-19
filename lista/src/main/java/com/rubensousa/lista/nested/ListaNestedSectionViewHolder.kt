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

package com.rubensousa.lista.nested

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.rubensousa.lista.ListaSectionViewHolder

/**
 * A ViewHolder for nested RecyclerViews.
 *
 * Scroll position is saved and restored automatically using [ListaScrollStateManager]
 * if [saveScrollState] returns true.
 *
 * If [recycleChildrenOnDetach] returns true, scroll state will be saved in [onDetachedFromWindow]
 * and restored in [onAttachedToWindow].
 * This should have the same value as [LinearLayoutManager.getRecycleChildrenOnDetach]
 *
 * Provide a unique scroll state key in [getScrollStateKey] based on the item [T]
 */
abstract class ListaNestedSectionViewHolder<T>(
    itemView: View,
    private val recycledViewPool: RecyclerView.RecycledViewPool,
    private val scrollStateManager: ListaScrollStateManager
) : ListaSectionViewHolder<T>(itemView) {

    /**
     * @return the RecyclerView that'll draw the items of this section
     */
    abstract fun getRecyclerView(): RecyclerView

    /**
     * @return the adapter that's bound to the RecyclerView provided in [getRecyclerView]
     */
    abstract fun getAdapter(): RecyclerView.Adapter<*>

    /**
     * @return an unique key per section to make sure scroll state works correctly
     */
    abstract fun getScrollStateKey(item: T): String

    /**
     * Replace the adapter contents using notifyDataSetChanged() or related methods
     */
    abstract fun updateAdapter(item: T)

    @CallSuper
    override fun onCreated() {
        super.onCreated()
        val recyclerView = getRecyclerView()

        // Set a shared view pool to recycle views across multiple RecyclerViews
        recyclerView.setRecycledViewPool(recycledViewPool)

        if (saveScrollState()) {
            // Registers a scroll listener in this RecyclerView to determine
            // if we need to save the scroll state
            scrollStateManager.setupRecyclerView(recyclerView)
        }
    }

    override fun onBind(item: T, payloads: List<Any>) {
        super.onBind(item, payloads)
        // If you support partial changes, you can override this to avoid
        // dispatching a full list update
        bindList(item)
    }

    @CallSuper
    override fun onBind(item: T) {
        super.onBind(item)
        bindList(item)
    }

    fun bindList(item: T) {
        updateAdapter(item)
        getRecyclerView().adapter = getAdapter()
        scrollStateManager.setScrollStateKey(getRecyclerView(), getScrollStateKey(item))
        if (saveScrollState()) {
            scrollStateManager.restoreScrollState(getRecyclerView())
        }
    }

    @CallSuper
    override fun onRecycled() {
        if (saveScrollState()) {
            scrollStateManager.saveScrollState(getRecyclerView())
            scrollStateManager.setScrollStateKey(getRecyclerView(), null)
        }
        super.onRecycled()
        // By setting the adapter to null,
        // we make sure the ViewHolders get a call to onDetachedFromWindow
        getRecyclerView().adapter = null
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (saveScrollState() && recycleChildrenOnDetach()) {
            scrollStateManager.restoreScrollState(getRecyclerView())
        }
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (saveScrollState() && recycleChildrenOnDetach()) {
            scrollStateManager.saveScrollState(getRecyclerView())
        }
        /**
         * If we fast scroll while this ViewHolder's RecyclerView is still settling the scroll,
         * the view will be detached and won't be snapped correctly.
         * To fix that, we snap again without smooth scrolling.
         */
        val snapHelper = getSnapHelper() ?: return
        val lm = getRecyclerView().layoutManager ?: return
        snapHelper.findSnapView(lm)?.let {
            val snapDistance = snapHelper.calculateDistanceToFinalSnap(lm, it)
            if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
                getRecyclerView().scrollBy(snapDistance[0], snapDistance[1])
            }
        }
    }

    /**
     * @return true if scroll state should be saved
     */
    open fun saveScrollState() = true

    /**
     * @return true if the LayoutManager is set to recycle views when they're detached from the window
     */
    open fun recycleChildrenOnDetach() = true

    /**
     * @return the SnapHelper attached to the RecyclerView in [getRecyclerView] or null
     */
    open fun getSnapHelper(): SnapHelper? = null

}
