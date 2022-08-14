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
import com.rubensousa.lista.ListaViewHolder

/**
 * A ViewHolder for nested RecyclerViews.
 *
 * Scroll position is saved and restored automatically using [ListaScrollStateManager]
 * if [isScrollStateSaveEnabled] returns true.
 *
 * If [isRecyclingChildrenOnDetachedFromWindow] returns true, scroll state will be saved in [onDetachedFromWindow]
 * and restored in [onAttachedToWindow].
 * This should have the same value as [LinearLayoutManager.getRecycleChildrenOnDetach]
 *
 * Provide a unique scroll state key in [getScrollStateKey] based on the item [T]
 */
abstract class ListaNestedViewHolder<T>(itemView: View) : ListaViewHolder<T>(itemView) {

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
     * Replace the adapter contents using notifyDataSetChanged() or related methods.
     * Do not worry about any actual diffing at this stage,
     * because the adapter will be completely replaced
     */
    abstract fun updateAdapter(item: T)

    /**
     * The RecycledViewPool to use for this ViewHolder, or null to use the default one.
     * For maximum performance, consider using a shared [RecyclerView.RecycledViewPool]
     */
    open fun getRecycledViewPool(): RecyclerView.RecycledViewPool? {
        return null
    }

    @CallSuper
    override fun onCreated() {
        super.onCreated()
        val recyclerView = getRecyclerView()

        // Set a shared view pool if any to recycle views across multiple RecyclerViews
        val recycledViewPool = getRecycledViewPool()
        if (recycledViewPool != null) {
            recyclerView.setRecycledViewPool(recycledViewPool)
        }
    }

    @CallSuper
    override fun onBound(item: T, payloads: List<Any>) {
        super.onBound(item, payloads)
        updateAdapter(item)
        getRecyclerView().adapter = getAdapter()
    }

    @CallSuper
    override fun onRecycled() {
        super.onRecycled()
        // By setting the adapter to null,
        // we make sure the ViewHolders get a call to onDetachedFromWindow
        getRecyclerView().adapter = null
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        /**
         * If we fast scroll while this ViewHolder's RecyclerView is still settling the scroll,
         * the view will be detached and won't be snapped correctly.
         * To fix that, we snap again without smooth scrolling.
         */
        restoreSnapPosition()
    }


    /**
     * @return true if scroll state should be saved
     */
    open fun isScrollStateSaveEnabled() = true

    /**
     * @return true if the LayoutManager is set to recycle views
     * when they're detached from the window.
     * When this is true, the scroll state needs to be restored at [onAttachedToWindow]
     * and saved in [onDetachedFromWindow]
     */
    open fun isRecyclingChildrenOnDetachedFromWindow() = true

    /**
     * @return the SnapHelper attached to the RecyclerView in [getRecyclerView] or null
     */
    open fun getSnapHelper(): SnapHelper? = null

    private fun restoreSnapPosition() {
        val snapHelper = getSnapHelper() ?: return
        val layout = getRecyclerView().layoutManager ?: return
        snapHelper.findSnapView(layout)?.let { view ->
            val snapDistance = snapHelper.calculateDistanceToFinalSnap(layout, view) ?: return@let
            if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                getRecyclerView().scrollBy(snapDistance[0], snapDistance[1])
            }
        }
    }

}
