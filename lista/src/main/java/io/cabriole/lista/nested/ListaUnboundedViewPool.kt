/*
 * Copyright 2016 Airbnb, Inc.
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

package io.cabriole.lista.nested

import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Like its parent, [ListaUnboundedViewPool] lets you share Views between multiple RecyclerViews.
 * However there is no maximum number of recycled views that it will store.
 * This usually ends up being optimal, barring any hard memory constraints,
 * as RecyclerViews do not recycle more Views than they need.
 */
class ListaUnboundedViewPool : RecyclerView.RecycledViewPool() {

    private val scrapHeaps = SparseArray<Queue<RecyclerView.ViewHolder>>()

    override fun clear() {
        scrapHeaps.clear()
    }

    override fun setMaxRecycledViews(viewType: Int, max: Int) {
        throw UnsupportedOperationException(
            "ListaUnboundedViewPool does not support setting a maximum number of recycled views"
        )
    }

    override fun getRecycledView(viewType: Int): RecyclerView.ViewHolder? {
        val scrapHeap = scrapHeaps.get(viewType)
        return scrapHeap?.poll()
    }

    override fun getRecycledViewCount(viewType: Int): Int {
        val scrapHeap = scrapHeaps.get(viewType)
        return scrapHeap?.size ?: 0
    }

    override fun putRecycledView(viewHolder: RecyclerView.ViewHolder) {
        getOrCreateScrapHeapForType(viewHolder.itemViewType).add(viewHolder)
    }

    private fun getOrCreateScrapHeapForType(viewType: Int): Queue<RecyclerView.ViewHolder> {
        var scrapHeap: Queue<RecyclerView.ViewHolder>? = scrapHeaps.get(viewType)
        if (scrapHeap == null) {
            scrapHeap = LinkedList()
            scrapHeaps.put(viewType, scrapHeap)
        }
        return scrapHeap
    }
}
