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

package com.rubensousa.lista.paging

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * Emits [ListaPage] objects when a RecyclerView attached is scrolled.
 *
 * Pass this class in [RecyclerView.addOnScrollListener] to start listening for page changes.
 *
 * @param pageListener a listener that receives the page change events
 *
 * @param linearLayoutManager the [LinearLayoutManager] attached to the RecyclerView
 */
class ListaPager(
    private val pageListener: OnPageChangedListener,
    private var linearLayoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    interface OnPageChangedListener {
        fun onPageChanged(page: ListaPage)
    }

    private val page = ListaPage(firstVisiblePosition = 0, lastVisiblePosition = 0)

    fun setLayoutManager(layoutManager: LinearLayoutManager) {
        linearLayoutManager = layoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()

        // Check for invalid positions
        if (firstVisiblePosition == RecyclerView.NO_POSITION ||
            lastVisiblePosition == RecyclerView.NO_POSITION
        ) {
            return
        }
        // Check for non-scroll events
        if ((dx == 0 && linearLayoutManager.orientation == RecyclerView.HORIZONTAL)
            || (dy == 0 && linearLayoutManager.orientation == RecyclerView.VERTICAL)
        ) {
            return
        }
        // Just notify if the new page is different
        if (firstVisiblePosition != page.firstVisiblePosition
            || lastVisiblePosition != page.lastVisiblePosition
        ) {
            page.firstVisiblePosition = firstVisiblePosition
            page.lastVisiblePosition = lastVisiblePosition
            pageListener.onPageChanged(page.copy())
        }
    }

    fun getCurrentPage(): ListaPage {
        return page.copy()
    }

}
