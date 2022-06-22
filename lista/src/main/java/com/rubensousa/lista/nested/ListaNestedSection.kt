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

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaSectionViewHolder

/**
 * A [ListaSection] for nested RecyclerViews.
 *
 * @param layoutId the layout id to be inflated
 *
 * @param recycledViewPool a shared [RecyclerView.RecycledViewPool] to recycle Views
 * between multiple RecyclerViews
 *
 * @param scrollStateManager a shared [ListaScrollStateManager] to persist the scroll state
 */
abstract class ListaNestedSection<T, VH: ListaSectionViewHolder<T>>(
    @LayoutRes layoutId: Int,
    protected val recycledViewPool: RecyclerView.RecycledViewPool = ListaUnboundedViewPool(),
    protected val scrollStateManager: ListaScrollStateManager = ListaScrollStateManager()
) : ListaSection<T, VH>(layoutId) {

    /**
     * Clears the scroll state of [scrollStateManager].
     * Scroll positions will be reset after the views are recycled and bound again.
     */
    fun clearScrollState() {
        scrollStateManager.clear()
    }

}
