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

package com.rubensousa.lista.item

import android.view.ViewGroup
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaViewHolder

class ListaItemSection<T, V : ListaViewHolder<T>>(
    private val onCreated: (holder: V) -> Unit = {},
    private val onBound: (holder: V, item: T, payloads: List<Any>) -> Unit = { _, _, _ -> },
    private val onRecycled: (holder: V) -> Unit = {},
    private val onAttached: (holder: V) -> Unit = {},
    private val onDetached: (holder: V) -> Unit = {},
    itemViewType: Int = VIEW_TYPE_AUTO_GENERATED,
    private val viewHolderCreator: (parent: ViewGroup) -> V,
) : ListaSection<T, V>(itemViewType) {

    override fun onCreateViewHolder(parent: ViewGroup): V {
        return viewHolderCreator(parent)
    }

    override fun onViewHolderCreated(holder: V) {
        onCreated(holder)
    }

    override fun onViewHolderBound(holder: V, item: T, payloads: List<Any>) {
        onBound(holder, item, payloads)
    }

    override fun onViewHolderAttachedToWindow(holder: V) {
        onAttached(holder)
    }

    override fun onViewHolderDetachedFromWindow(holder: V) {
        onDetached(holder)
    }

    override fun onViewHolderRecycled(holder: V) {
        onRecycled(holder)
    }


}
