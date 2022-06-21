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

/**
 * A ViewHolder for a [ListaSection].
 */
interface ListaViewHolder<T> {

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onCreateViewHolder]
     */
    fun onCreated() {}

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onBindViewHolder]
     *
     * @param item the item from the adapter that needs to be bound
     * @param payloads a non-empty list for merged payloads
     */
    fun onBind(item: T, payloads: List<Any>) {}

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onViewRecycled]
     */
    fun onRecycled() {}

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onFailedToRecycleView]
     */
    fun onFailedToRecycle(): Boolean = false

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onViewAttachedToWindow]
     */
    fun onAttachedToWindow() {}

    /**
     * Called after [androidx.recyclerview.widget.RecyclerView.Adapter.onViewDetachedFromWindow]
     */
    fun onDetachedFromWindow() {}

    /**
     * Implemented by [androidx.recyclerview.widget.RecyclerView.ViewHolder]
     */
    fun getItemViewType(): Int

}
