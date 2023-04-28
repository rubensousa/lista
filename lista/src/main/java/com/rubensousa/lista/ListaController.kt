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

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.rubensousa.lista.section.ListaSectionRegistry

/**
 * A controller for a RecyclerView that displays a list of items of type [T].
 *
 * Adapter changes are only dispatched when it's safe to do so.
 *
 * Override the relevant setup methods and then call [setup] with your RecyclerView:
 *
 * - [createSectionRegistry]: return the [ListaSectionRegistry] used to find the relevant sections
 * for each item of the adapter
 */
abstract class ListaController<T>(
    private val lifecycle: Lifecycle
) {

    private val updateDispatcher = ListaUpdateDispatcher<T>()
    private val adapterListener = AsyncListDiffer.ListListener<T> { _, _ ->
        // Invalidate item decorations after adapter changes
        recyclerView?.invalidateItemDecorations()
    }
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            onLifecycleDestroy()
        }
    }
    private var recyclerView: RecyclerView? = null

    /**
     * The adapter that'll be bound to the [RecyclerView]
     *
     * This will only be available after a call to [setup]
     */
    private lateinit var adapter: ListaAdapter<T>

    abstract fun createSectionRegistry(
        adapter: ListaAdapter<T>,
        recyclerView: RecyclerView
    ): ListaSectionRegistry<T>

    abstract fun createDiffItemCallback(): DiffUtil.ItemCallback<T>

    open fun createAdapter(): ListaAdapter<T> {
        return ListaAdapter(createDiffItemCallback())
    }

    /**
     * @return the layout manager to be used by the RecyclerView bound in [setup]
     */
    open fun createLayoutManager(context: Context): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    /**
     * @return the item decorations to be used by the RecyclerView bound in [setup]
     */
    open fun createItemDecorations(layoutManager: RecyclerView.LayoutManager)
            : List<RecyclerView.ItemDecoration> {
        return emptyList()
    }

    /**
     * @return the item animator to be used by the RecyclerView bound in [setup]
     */
    open fun createItemAnimator(): RecyclerView.ItemAnimator? {
        return DefaultItemAnimator()
    }

    /**
     * @return a custom [RecyclerView.RecycledViewPool] or null if the default one should be used
     */
    open fun getRecycledViewPool(): RecyclerView.RecycledViewPool? {
        return null
    }

    /**
     * If the RecyclerView always fits the whole width/height, this should be true
     */
    open fun hasFixedSize() = true

    @CallSuper
    open fun onLifecycleDestroy() {
        val lm = recyclerView?.layoutManager

        // This makes sure all children will dispatch onDetachedToWindow to their ViewHolders
        // and that all children are available for re-use in case there's a shared view pool
        if (lm is LinearLayoutManager) {
            lm.recycleChildrenOnDetach = true
        }

        adapter.clearOnListChangedListeners()
        recyclerView?.let { updateDispatcher.destroy(it) }
        recyclerView?.layoutManager = null
        recyclerView?.adapter = null
        recyclerView = null
    }

    fun isEmpty() = adapter.itemCount == 0

    fun getAdapter() = adapter

    open fun setup(recyclerView: RecyclerView) {
        // Check if we already setup a RecyclerView
        if (this.recyclerView != null) {
            return
        }

        // We want to make sure all ViewHolders get a call to onDetachedFromWindow
        // when the lifecycle is destroyed
        lifecycle.addObserver(lifecycleObserver)

        adapter = createAdapter()
        adapter.addOnListChangedListener(adapterListener)

        // Set the default LayoutManager
        val layoutManager = createLayoutManager(recyclerView.context)
        recyclerView.layoutManager = layoutManager

        val recycledViewPool = getRecycledViewPool()
        if (recycledViewPool != null) {
            recyclerView.setRecycledViewPool(recycledViewPool)
        }

        // Add the sections for this adapter.
        // The LayoutManager needs to be set before, since some sections might need access to it
        adapter.setSectionRegistry(createSectionRegistry(adapter, recyclerView))

        // If the RecyclerView always fits the whole width/height, this should be true
        recyclerView.setHasFixedSize(hasFixedSize())

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = createItemAnimator()

        // Apply default item decorations
        val itemDecorations = createItemDecorations(layoutManager)
        itemDecorations.forEach {
            recyclerView.addItemDecoration(it)
        }

        this.recyclerView = recyclerView
        updateDispatcher.setup(recyclerView)
    }

    /**
     * If the RecyclerView is still computing layout, scrolling or animating,
     * the new items won't be dispatched immediately in that case.
     *
     * When [applyDiffing] is set to false, [ListaAdapter.notifyDataSetChanged] is called
     * instead of performing individual updates like [ListaAdapter.notifyItemChanged]
     */
    open fun submitList(
        items: List<T>,
        applyDiffing: Boolean = true
    ) {
        recyclerView?.let { currentRecyclerView ->
            updateDispatcher.update(
                currentRecyclerView, adapter, items, applyDiffing,
                supportsDispatchingItemsDuringScroll()
            )
        }
    }

    open fun supportsDispatchingItemsDuringScroll(): Boolean {
        return true
    }

}
