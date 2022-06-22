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
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.*
import com.rubensousa.lista.section.ClassSectionRegistry
import com.rubensousa.lista.section.ListaSectionRegistry
import java.util.*

/**
 * A [ListaAdapter] is a RecyclerView adapter that draws a [ListaSection] for each view type.
 *
 * Sections will be created and bound to adapter items based on the [ListaSectionRegistry]
 * set via [setSectionRegistry].
 *
 * Each [ListaSection] needs to return an unique itemViewType,
 * which is the layout resource id by default.
 *
 * When the adapter contents are changed through [submitList],
 * [ListaAsyncDiffer] kicks-in to calculate the diff between the previous list and the new list.
 */
open class ListaAdapter<T>(
    diffItemCallback: DiffUtil.ItemCallback<T>,
    defaultSectionRegistry: ListaSectionRegistry = ClassSectionRegistry()
) : RecyclerView.Adapter<ListaSectionViewHolder<T>>() {

    private val differ: ListaAsyncDiffer<T>
    private val updateCallback: AdapterUpdateCallback = AdapterUpdateCallback()
    private var sectionRegistry: ListaSectionRegistry = defaultSectionRegistry

    init {
        differ = ListaAsyncDiffer(
            updateCallback,
            AsyncDifferConfig.Builder(diffItemCallback).build()
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaSectionViewHolder<T> {
        return getSection(viewType).onCreateViewHolder(parent)
            .also { holder: ListaSectionViewHolder<T> ->
                holder.onCreated()
            }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItemAt(position)
        return sectionRegistry.getSectionForItem(item)?.getItemViewType()
            ?: throw IllegalStateException(
                "Section not found for position: " + position
                        + "\nItem found: " + item!!::class.java.simpleName
            )
    }

    override fun onBindViewHolder(holder: ListaSectionViewHolder<T>, position: Int) {
        getSection(holder).onBindViewHolder(holder, getItemAt(position), Collections.emptyList())
    }

    override fun onBindViewHolder(
        holder: ListaSectionViewHolder<T>, position: Int, payloads: MutableList<Any>
    ) {
        getSection(holder).onBindViewHolder(holder, getItemAt(position), payloads)
    }

    override fun onViewRecycled(holder: ListaSectionViewHolder<T>) {
        getSection(holder).onViewRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: ListaSectionViewHolder<T>) {
        getSection(holder).onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ListaSectionViewHolder<T>) {
        getSection(holder).onViewDetachedFromWindow(holder)
    }

    override fun onFailedToRecycleView(holder: ListaSectionViewHolder<T>): Boolean {
        return getSection(holder).onFailedToRecycleView(holder)
    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }

    fun setSectionRegistry(newSectionRegistry: ListaSectionRegistry) {
        sectionRegistry = newSectionRegistry
    }

    fun getItemAt(position: Int): T = differ.getCurrentList()[position]

    fun clear() {
        submit(listOf())
    }

    /**
     * Replaces the items of the adapter.
     *
     * This will dispatch the new list in another thread
     * and calculate a diff of the old list and the new one
     *
     * @param items the new items to be applied to the adapter
     */
    fun <V : T> submit(items: List<V>, commitCallback: Runnable? = null) {
        differ.submitList(items, commitCallback)
    }

    /**
     * Replaces the items of the adapter.
     * Use this when you're inside a nested RecyclerView to avoid triggering unnecessary work,
     * since you can set the items before you bind the adapter.
     *
     * If you want to get the appropriate updates by diffing the old list and the new one,
     * use [submit]
     *
     * @param items the new items to be applied to the adapter
     */
    fun <V : T> submitNow(items: List<V>) {
        differ.submitNow(this, items)
    }

    /**
     * Add a ListListener to receive updates when the current adapter list changes.
     */
    fun addOnListChangedListener(listener: AsyncListDiffer.ListListener<T>) {
        differ.addListListener(listener)
    }

    /**
     * Remove a previously registered ListListener.
     */
    fun removeOnListChangedListener(listener: AsyncListDiffer.ListListener<T>) {
        differ.removeListListener(listener)
    }

    /**
     * Removes all previously registered ListListeners
     */
    fun clearOnListChangedListeners() {
        differ.clearListListeners()
    }

    private fun getSection(holder: ListaSectionViewHolder<T>)
            : ListaSection<T, ListaSectionViewHolder<T>> {
        @Suppress("UNCHECKED_CAST")
        return (sectionRegistry.getSectionForItemViewType(holder.itemViewType)
            ?: throw IllegalStateException(
                "No section found for ViewHolder at ${holder.absoluteAdapterPosition} " +
                        "and viewType = ${holder.itemViewType}"
            )) as ListaSection<T, ListaSectionViewHolder<T>>
    }

    private fun getSection(itemViewType: Int): ListaSection<T, ListaSectionViewHolder<T>> {
        @Suppress("UNCHECKED_CAST")
        return (sectionRegistry.getSectionForItemViewType(itemViewType)
            ?: throw IllegalStateException(
                "No section found for itemViewType: $itemViewType"
            )) as ListaSection<T, ListaSectionViewHolder<T>>
    }

    private inner class AdapterUpdateCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

    }

    @VisibleForTesting
    fun setList(list: List<T>?) {
        differ.setList(list)
    }

}
