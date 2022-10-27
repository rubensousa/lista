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

import androidx.annotation.CallSuper
import com.rubensousa.lista.ListaSection

/**
 * A [ListaSection] for nested RecyclerViews.
 *
 * @param scrollStateManager a shared [ListaScrollStateManager] to persist the scroll state
 */
abstract class ListaNestedSection<T, VH : ListaNestedViewHolder<T>>(
    itemViewType: Int = VIEW_TYPE_AUTO_GENERATED,
    protected val scrollStateManager: ListaScrollStateManager = ListaScrollStateManager()
) : ListaSection<T, VH>(itemViewType) {

    /**
     * Clears the scroll state of [scrollStateManager].
     * Scroll positions will be reset after the views are recycled and bound again.
     */
    fun clearScrollState() {
        scrollStateManager.clear()
    }

    @CallSuper
    override fun onViewHolderCreated(holder: VH) {
        super.onViewHolderCreated(holder)
        if (holder.isScrollStateSaveEnabled()) {
            scrollStateManager.setupRecyclerView(holder.getRecyclerView())
        }
    }

    @CallSuper
    override fun onViewHolderBound(holder: VH, item: T, payloads: List<Any>) {
        super.onViewHolderBound(holder, item, payloads)
        val recyclerView = holder.getRecyclerView()
        scrollStateManager.setScrollStateKey(recyclerView, holder.getScrollStateKey(item))
        if (holder.isScrollStateSaveEnabled()) {
            scrollStateManager.restoreScrollState(recyclerView)
        }
    }

    @CallSuper
    override fun onViewHolderRecycled(holder: VH) {
        super.onViewHolderRecycled(holder)
        if (holder.isScrollStateSaveEnabled()) {
            scrollStateManager.saveScrollState(holder.getRecyclerView())
        }
        scrollStateManager.setScrollStateKey(holder.getRecyclerView(), null)
    }

    @CallSuper
    override fun onViewHolderAttachedToWindow(holder: VH) {
        super.onViewHolderAttachedToWindow(holder)
        if (holder.isScrollStateSaveEnabled() && holder.isRecyclingChildrenOnDetachedFromWindow()) {
            scrollStateManager.restoreScrollState(holder.getRecyclerView())
        }
    }

    @CallSuper
    override fun onViewHolderDetachedFromWindow(holder: VH) {
        super.onViewHolderDetachedFromWindow(holder)
        if (holder.isScrollStateSaveEnabled() && holder.isRecyclingChildrenOnDetachedFromWindow()) {
            scrollStateManager.saveScrollState(holder.getRecyclerView())
        }
    }

}
