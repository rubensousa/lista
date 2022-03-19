package com.rubensousa.lista

import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * A [GridLayoutManager.SpanSizeLookup] that defaults to [defaultSpanSize] if a span size
 * for a given item view type isn't found
 */
class ListaSpanLookup(
    private val adapter: RecyclerView.Adapter<*>,
    private var defaultSpanSize: Int = 1
) : GridLayoutManager.SpanSizeLookup() {

    private val spanSizes: SparseArrayCompat<Int> = SparseArrayCompat()

    fun setDefaultSpanSize(size: Int) {
        defaultSpanSize = size
    }

    fun setSpanSizeForSection(section: ListaSection<*>, size: Int) {
        setSpanSizeForItemViewType(section.getItemViewType(), size)
    }

    fun setSpanSizeForItemViewType(itemViewType: Int, size: Int) {
        spanSizes.put(itemViewType, size)
    }

    fun removeSpanSizeForItemViewType(itemViewType: Int) {
        spanSizes.remove(itemViewType)
    }

    fun removeSpanSizeForSection(section: ListaSection<*>) {
        removeSpanSizeForItemViewType(section.getItemViewType())
    }

    fun getSpanSizeForItemViewType(itemViewType: Int): Int {
        return spanSizes[itemViewType] ?: defaultSpanSize
    }

    fun clearSpanSizes() {
        spanSizes.clear()
    }

    override fun getSpanSize(position: Int): Int {
        val itemViewType = adapter.getItemViewType(position)
        return spanSizes[itemViewType] ?: defaultSpanSize
    }

}
