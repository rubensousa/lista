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

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat

/**
 * Delegates ViewHolder creation and binding for [ListaAdapter]
 */
class ListaSectionBinder<T : Any> {

    private val sections: SparseArrayCompat<ListaSection<T>> = SparseArrayCompat()

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaSectionViewHolder<T> {
        return requireSection<T>(viewType).onCreateViewHolder(parent)
    }

    fun onBindViewHolder(holder: ListaSectionViewHolder<T>, item: T) {
        requireSection<T>(holder.itemViewType).onBindViewHolder(holder, item)
    }

    fun onBindViewHolder(holder: ListaSectionViewHolder<T>, item: T, payloads: List<Any>) {
        requireSection<T>(holder.itemViewType).onBindViewHolder(holder, item, payloads)
    }

    fun getItemViewType(item: T, position: Int): Int {
        for (i in 0 until sections.size()) {
            val section = sections.valueAt(i)
            if (section.isForItem(item)) {
                return section.getItemViewType()
            }
        }
        throw IllegalStateException(
            "Section not found for position: " + position
                    + "\nItem found: " + item.javaClass.simpleName
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : T> addSection(section: ListaSection<V>) {
        sections.put(section.getItemViewType(), section as ListaSection<T>)
    }

    fun <V : T> removeSection(section: ListaSection<V>) {
        sections.remove(section.getItemViewType())
    }

    fun removeAllSections() {
        sections.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : T> getSection(viewType: Int): ListaSection<V>? {
        return sections.get(viewType) as ListaSection<V>?
    }

    fun getSections(): List<ListaSection<T>> {
        val output = arrayListOf<ListaSection<T>>()
        for (i in 0 until sections.size()) {
            output.add(sections.valueAt(i))
        }
        return output
    }

    private fun <V : T> requireSection(viewType: Int): ListaSection<V> {
        return getSection(viewType)!!
    }

}
