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
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.core.view.children
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
open class ListaAdapter<T> @VisibleForTesting constructor(
    differConfig: ListaAsyncDiffer.Config<T>,
    updateCallback: ListaAsyncDiffer.UpdateCallback? = null
) : RecyclerView.Adapter<ListaViewHolder<T>>() {

    private val differ: ListaAsyncDiffer<T> by lazy {
        ListaAsyncDiffer(updateCallback ?: AdapterUpdateCallback(this), differConfig)
    }
    private var sectionRegistry: ListaSectionRegistry = ClassSectionRegistry()

    constructor(
        diffItemCallback: DiffUtil.ItemCallback<T>
    ) : this(ListaAsyncDiffer.Config.build(diffItemCallback))

    constructor(
        differConfig: ListaAsyncDiffer.Config<T>,
    ) : this(differConfig, null) {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder<T> {
        val section = getSection(viewType)
        val viewHolder = section.onCreateViewHolder(parent)
        viewHolder.onCreated()
        section.onViewHolderCreated(viewHolder)
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItemAt(position)
        return sectionRegistry.getSectionForItem(item)?.getItemViewType()
            ?: throw IllegalStateException(
                "Section not found for position: " + position
                        + "\nItem found: " + item.toString()
            )
    }

    override fun onBindViewHolder(holder: ListaViewHolder<T>, position: Int) {
        val item = getItemAt(position)
        if (item != null) {
            holder.bind(item, Collections.emptyList())
            getSection(holder).onViewHolderBound(holder, item, Collections.emptyList())
        }
    }

    override fun onBindViewHolder(
        holder: ListaViewHolder<T>, position: Int, payloads: List<Any>
    ) {
        val item = getItemAt(position)
        if (item != null) {
            holder.bind(item, payloads)
            getSection(holder).onViewHolderBound(holder, item, payloads)
        }
    }

    override fun onViewRecycled(holder: ListaViewHolder<T>) {
        holder.recycle()
        getSection(holder).onViewHolderRecycled(holder)
    }

    override fun onViewAttachedToWindow(holder: ListaViewHolder<T>) {
        holder.onAttachedToWindow()
        getSection(holder).onViewHolderAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ListaViewHolder<T>) {
        holder.onDetachedFromWindow()
        getSection(holder).onViewHolderDetachedFromWindow(holder)
    }

    override fun onFailedToRecycleView(holder: ListaViewHolder<T>): Boolean {
        return holder.onFailedToRecycle()
    }

    override fun getItemCount(): Int {
        return differ.getCurrentList().size
    }

    fun setSectionRegistry(newSectionRegistry: ListaSectionRegistry) {
        sectionRegistry = newSectionRegistry
    }

    fun getSectionRegistry(): ListaSectionRegistry {
        return sectionRegistry
    }

    fun getItemAt(position: Int): T? = differ.getCurrentList()[position]

    @MainThread
    fun clear() {
        submit(listOf())
    }

    @MainThread
    fun clearNow() {
        submitNow(listOf())
    }

    /**
     * Replaces the items of the adapter.
     *
     * This will dispatch the new list in another thread
     * and calculate a diff of the old list and the new one
     *
     * @param items the new items to be applied to the adapter
     */
    @MainThread
    fun <V : T> submit(items: List<V?>, commitCallback: Runnable? = null) {
        differ.submitList(items, commitCallback)
    }

    /**
     * Replaces the items of the adapter.
     * Use this when you're inside a nested RecyclerView to avoid triggering unnecessary work,
     * since you can set the items before you bind the adapter.
     *
     * If you want to get the appropriate updates by diffing the old list and the new one,
     * use [submit] instead.
     *
     * @param items the new items to be applied to the adapter
     */
    @MainThread
    fun <V : T> submitNow(items: List<V?>) {
        differ.submitNow(items)
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

    fun findViewHoldersOfSection(
        section: ListaSection<*, *>,
        recyclerView: RecyclerView
    ): List<RecyclerView.ViewHolder> {
        val viewHolders = ArrayList<RecyclerView.ViewHolder>()
        recyclerView.children.forEach { child ->
            val viewHolder = recyclerView.getChildViewHolder(child)
            if (viewHolder != null && viewHolder.itemViewType == section.getItemViewType()) {
                viewHolders.add(viewHolder)
            }
        }
        return viewHolders
    }

    private fun getSection(holder: ListaViewHolder<T>): ListaSection<T, ListaViewHolder<T>> {
        @Suppress("UNCHECKED_CAST")
        return (sectionRegistry.getSectionForItemViewType(holder.itemViewType)
            ?: throw IllegalStateException(
                "No section found for ViewHolder at ${holder.absoluteAdapterPosition} " +
                        "and viewType = ${holder.itemViewType}"
            )) as ListaSection<T, ListaViewHolder<T>>
    }

    private fun getSection(itemViewType: Int): ListaSection<T, ListaViewHolder<T>> {
        @Suppress("UNCHECKED_CAST")
        return (sectionRegistry.getSectionForItemViewType(itemViewType)
            ?: throw IllegalStateException(
                "No section found for itemViewType: $itemViewType"
            )) as ListaSection<T, ListaViewHolder<T>>
    }

}
