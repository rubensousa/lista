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

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

/**
 * A ViewHolder for a [ListaSection].
 *
 * The item bound to this ViewHolder can be access via [getItem].
 * It'll be set in [onBind] and cleared in [onRecycled].
 */
abstract class ListaSectionViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var item: T? = null

    /**
     * @return the current item bound to this section
     * or null if the item hasn't been bound or was recycled recently
     */
    fun getItem(): T? = item

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onCreateViewHolder]
     */
    open fun onCreated() {
    }

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onBindViewHolder]
     */
    @CallSuper
    open fun onBind(item: T) {
        this.item = item
    }

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onBindViewHolder]
     *
     * @param item the item from the adapter that needs to be bound
     * @param payloads a non-empty list for merged payloads
     */
    @CallSuper
    open fun onBind(item: T, payloads: List<Any>) {
        this.item = item
    }

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onViewRecycled]
     */
    @CallSuper
    open fun onRecycled() {
        this.item = null
    }

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onFailedToRecycleView]
     */
    open fun onFailedToRecycle(): Boolean {
        return false
    }

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onViewAttachedToWindow]
     */
    open fun onAttachedToWindow() {

    }

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onViewDetachedFromWindow]
     */
    open fun onDetachedFromWindow() {

    }

}
