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

import android.view.ViewGroup

/**
 * Delegates ViewHolder creation and binding for [ListaAdapter]
 */
class ListaSectionBinder<T : Any> {

    private val sections = LinkedHashMap<Int, ListaSection<*>>()

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaSectionViewHolder<T> {
        return requireSection<T>(viewType).onCreateViewHolder(parent)
    }

    fun onBindViewHolder(holder: ListaSectionViewHolder<T>, item: T) {
        requireSection<T>(holder).onBindViewHolder(holder, item)
    }

    fun onBindViewHolder(holder: ListaSectionViewHolder<T>, item: T, payloads: List<Any>) {
        requireSection<T>(holder).onBindViewHolder(holder, item, payloads)
    }

    fun onViewRecycled(holder: ListaSectionViewHolder<T>) {
        requireSection<T>(holder).onViewRecycled(holder)
    }

    fun onViewAttachedToWindow(holder: ListaSectionViewHolder<T>) {
        requireSection<T>(holder).onViewAttachedToWindow(holder)
    }

    fun onViewDetachedFromWindow(holder: ListaSectionViewHolder<T>) {
        requireSection<T>(holder).onViewDetachedFromWindow(holder)
    }

    fun onFailedToRecycleView(holder: ListaSectionViewHolder<T>): Boolean {
        return requireSection<T>(holder).onFailedToRecycleView(holder)
    }

    fun getItemViewType(item: T, position: Int): Int {
        sections.entries.forEach { entry ->
            val section = entry.value
            val itemViewType = entry.key
            if (section.isForItem(item)) {
                return itemViewType
            }
        }
        throw IllegalStateException(
            "Section not found for position: " + position
                    + "\nItem found: " + item.javaClass.simpleName
        )
    }

    fun <V : T> addSection(section: ListaSection<V>) {
        sections[section.getItemViewType()] = section
    }

    fun <V : T> removeSection(section: ListaSection<V>) {
        sections.remove(section.getItemViewType())
    }

    fun removeAllSections() {
        sections.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : T> getSection(viewType: Int): ListaSection<V>? {
        return sections[viewType] as ListaSection<V>?
    }

    fun getSections(): List<ListaSection<*>> {
        val output = arrayListOf<ListaSection<*>>()
        output.addAll(sections.values)
        return output
    }

    private fun <V : T> requireSection(holder: ListaSectionViewHolder<*>): ListaSection<V> {
        return getSection(holder.itemViewType)
            ?: throw IllegalStateException(
                "No section found for ViewHolder at ${holder.absoluteAdapterPosition} " +
                        "and viewType = ${holder.itemViewType}"
            )
    }

    private fun <V : T> requireSection(viewType: Int): ListaSection<V> {
        return getSection(viewType)!!
    }

}
