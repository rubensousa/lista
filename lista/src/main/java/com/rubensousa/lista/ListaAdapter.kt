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

/**
 * A [ListaAdapter] is a RecyclerView adapter that draws a [ListaSection] for each view type.
 *
 * Each [ListaSection] can be added with [addSection] and removed with [removeSection]
 * and must provide a unique item view type in [ListaSection.getItemViewType],
 * which is the layout resource id by default.
 *
 * When the adapter contents are changed through [submitList],
 * [ListaAsyncDiffer] kicks-in to calculate the diff between the previous list and the new list.
 *
 */
open class ListaAdapter<T : Any>(
    diffItemCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<ListaSectionViewHolder<T>>() {

    private val differ: ListaAsyncDiffer<T>
    private val sectionBinder: ListaSectionBinder<T> = ListaSectionBinder()
    private val updateCallback: AdapterUpdateCallback = AdapterUpdateCallback()

    init {
        differ = ListaAsyncDiffer(
            updateCallback,
            AsyncDifferConfig.Builder(diffItemCallback).build()
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaSectionViewHolder<T> {
        return sectionBinder.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return sectionBinder.getItemViewType(getItemAt(position), position)
    }

    override fun onBindViewHolder(holder: ListaSectionViewHolder<T>, position: Int) {
        sectionBinder.onBindViewHolder(holder, getItemAt(position))
    }

    override fun onBindViewHolder(
        holder: ListaSectionViewHolder<T>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            sectionBinder.onBindViewHolder(holder, getItemAt(position), payloads)
        } else {
            sectionBinder.onBindViewHolder(holder, getItemAt(position))
        }
    }

    override fun onViewRecycled(holder: ListaSectionViewHolder<T>) {
        sectionBinder.onViewRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: ListaSectionViewHolder<T>) {
        sectionBinder.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ListaSectionViewHolder<T>) {
        sectionBinder.onViewDetachedFromWindow(holder)
    }

    override fun onFailedToRecycleView(holder: ListaSectionViewHolder<T>): Boolean {
        return sectionBinder.onFailedToRecycleView(holder)
    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }

    open fun <V : T> addSection(section: ListaSection<V>) {
        sectionBinder.addSection(section)
    }

    open fun <V : T> removeSection(section: ListaSection<V>) {
        sectionBinder.removeSection(section)
    }

    fun removeAllSections() {
        sectionBinder.removeAllSections()
    }

    fun getSections(): List<ListaSection<*>> {
        return sectionBinder.getSections()
    }

    fun getSection(viewType: Int): ListaSection<T>? {
        return sectionBinder.getSection(viewType)
    }

    fun getItemAt(position: Int): T = differ.getCurrentList()[position]

    fun clear() {
        submitList(arrayListOf())
    }

    /**
     * Replaces the items of the adapter.
     * If you're using this inside a nested RecyclerView, use [applyDiffing] as false
     * to avoid triggering unnecessary work, since you can set the items
     * before you bind the adapter.
     *
     * @param items the new items to be applied to the adapter
     * @param applyDiffing true if DiffUtil should be used for fine grained updates, false otherwise
     */
    open fun <V : T> submitList(items: List<V>, applyDiffing: Boolean = true) {
        if (applyDiffing) {
            differ.submitList(items)
        } else {
            differ.submitImmediately(this, items)
        }
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
