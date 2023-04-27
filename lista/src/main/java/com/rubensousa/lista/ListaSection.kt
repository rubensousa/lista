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
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding

/**
 * A [ListaSection] takes care of showing sections in a [ListaAdapter].
 *
 * Each section needs to provide a unique view type in [getItemViewType],
 * which is [layoutId] by default.
 *
 * By default, the [itemViewType] is generated internally by [ListaAdapter]
 * based on the sections registered, but is set initially to [VIEW_TYPE_AUTO_GENERATED].
 *
 * If you're using a shared RecycledViewPool, you should not use [VIEW_TYPE_AUTO_GENERATED]
 * and instead make the [itemViewType] predetermined and stable.
 * Example: the resource id of the layout you're going to inflate later.
 */
abstract class ListaSection<T, V : ListaViewHolder<T>>(
    private var itemViewType: Int = VIEW_TYPE_AUTO_GENERATED
) {

    companion object {
        const val VIEW_TYPE_AUTO_GENERATED = -1
    }

    /**
     * @return the ViewHolder to be used by this Section
     */
    abstract fun onCreateViewHolder(parent: ViewGroup): V

    /**
     * @return the item view type for [ListaAdapter]. Must be unique per Adapter
     */
    open fun getItemViewType(): Int {
        return itemViewType
    }

    open fun onViewHolderCreated(holder: V) {}

    open fun onViewHolderBound(holder: V, item: T, payloads: List<Any>) {}

    open fun onViewHolderRecycled(holder: V) {}

    open fun onViewHolderAttachedToWindow(holder: V) {}

    open fun onViewHolderDetachedFromWindow(holder: V) {}

    /**
     * Helper function to inflate the layout specified in [layoutId]
     * @return the View that'll be used by the ViewHolder
     */
    protected fun inflate(parent: ViewGroup, layoutId: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    }

    protected fun <T: ViewBinding> ViewGroup.bindingOf(
        factory: (LayoutInflater, ViewGroup, Boolean) -> T
    ): T {
        return factory(LayoutInflater.from(context), this, false)
    }

    /**
     * Is set by the section registry when [VIEW_TYPE_AUTO_GENERATED] is defined as [itemViewType]
     */
    internal fun setGeneratedItemViewType(value: Int) {
        itemViewType = value
    }

}
