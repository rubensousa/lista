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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * A [ListaSection] takes care of showing sections in a [ListaAdapter].
 *
 * Each section needs to provide a unique view type in [getItemViewType],
 * which is [layoutId] by default.
 */
abstract class ListaSection<T>(@LayoutRes val layoutId: Int) {

    /**
     * @return the ViewHolder to be used by this Section
     */
    abstract fun onCreateViewHolder(view: View): ListaSectionViewHolder<T>

    /**
     * @return true if this Section should be used to bind [item] or false otherwise
     */
    abstract fun isForItem(item: Any): Boolean

    /**
     * @return the item view type for [ListaAdapter]. Must be unique per Adapter
     */
    open fun getItemViewType(): Int {
        return layoutId
    }

    /**
     * @return the View that'll be used by the ViewHolder
     */
    open fun inflateLayout(parent: ViewGroup, layoutId: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    }

    /**
     * @return the ViewHolder to be used by this Section
     */
    open fun onCreateViewHolder(parent: ViewGroup): ListaSectionViewHolder<T> {
        val holder = onCreateViewHolder(inflateLayout(parent, layoutId))
        holder.onCreated()
        return holder
    }

    open fun onBindViewHolder(holder: ListaSectionViewHolder<T>, item: T) {
        holder.onBind(item)
    }

    open fun onBindViewHolder(holder: ListaSectionViewHolder<T>, item: T, payloads: List<Any>) {
        holder.onBind(item, payloads)
    }

}
