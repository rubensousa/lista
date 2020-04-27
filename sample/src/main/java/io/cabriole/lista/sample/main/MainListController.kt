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

package io.cabriole.lista.sample.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.cabriole.decorator.DecorationLookup
import io.cabriole.decorator.GridSpanMarginDecoration
import io.cabriole.decorator.LinearBoundsMarginDecoration
import io.cabriole.decorator.LinearMarginDecoration
import io.cabriole.lista.ListaAdapter
import io.cabriole.lista.ListaController
import io.cabriole.lista.ListaSpanLookup
import io.cabriole.lista.nested.ListaScrollStateManager
import io.cabriole.lista.nested.ListaUnboundedViewPool
import io.cabriole.lista.sample.R
import io.cabriole.lista.sample.model.CardListModel
import io.cabriole.lista.sample.model.CardModel
import io.cabriole.lista.sample.model.OptionModel
import io.cabriole.lista.sample.model.SectionModel
import io.cabriole.lista.sample.sections.*

class MainListController(
    private val fragment: Fragment,
    private val scrollStateManager: ListaScrollStateManager
) : ListaController<SectionModel>(fragment.viewLifecycleOwner.lifecycle),
    OptionSection.OnOptionClickListener {

    override fun createDiffItemCallback(): DiffUtil.ItemCallback<SectionModel> {
        return ListModelDiffCallback()
    }

    override fun createLayoutManager(context: Context): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 2)
    }

    override fun createRecycledViewPool(): RecyclerView.RecycledViewPool? {
        return ListaUnboundedViewPool()
    }

    override fun hasFixedSize(): Boolean {
        return true
    }

    override fun addSections(adapter: ListaAdapter<SectionModel>, recyclerView: RecyclerView) {
        val cardSection = CardSection(
            layout = R.layout.section_card_grid,
            showPosition = false
        )
        adapter.addSection(OptionSection(this))
        adapter.addSection(HeaderSection())
        adapter.addSection(CardListSection(recyclerView.recycledViewPool, scrollStateManager))
        adapter.addSection(cardSection)

        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val spanSizeLookup = ListaSpanLookup(adapter, defaultSpanSize = layoutManager.spanCount)
        spanSizeLookup.setSpanSizeForSection(cardSection, 1)
        layoutManager.spanSizeLookup = spanSizeLookup
    }

    override fun createItemDecorations(layoutManager: RecyclerView.LayoutManager)
            : List<RecyclerView.ItemDecoration> {
        val gridLayoutManager = layoutManager as GridLayoutManager
        val boundsDecorationSize = fragment.resources.getDimensionPixelSize(
            R.dimen.default_edge_decoration_size
        )
        val defaultDecorationSize = fragment.resources.getDimensionPixelOffset(
            R.dimen.default_decoration_size
        )
        return listOf(
            GridSpanMarginDecoration(
                margin = defaultDecorationSize,
                gridLayoutManager = gridLayoutManager,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        val item = getAdapter().getItemAt(position)
                        return item is CardModel
                    }
                }
            ),
            LinearMarginDecoration(
                topMargin = defaultDecorationSize,
                bottomMargin = defaultDecorationSize,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return getAdapter().getItemAt(position) !is CardModel
                    }
                }
            ),
            LinearMarginDecoration(
                topMargin = defaultDecorationSize,
                bottomMargin = defaultDecorationSize,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return getAdapter().getItemAt(position) is OptionModel
                    }
                }
            ),
            LinearMarginDecoration(
                topMargin = boundsDecorationSize,
                bottomMargin = boundsDecorationSize,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(position: Int, itemCount: Int): Boolean {
                        return getAdapter().getItemAt(position) is CardListModel
                    }
                }
            ),
            LinearBoundsMarginDecoration(
                topMargin = boundsDecorationSize,
                bottomMargin = boundsDecorationSize
            )
        )
    }

    override fun onOptionClicked(optionModel: OptionModel) {
        Snackbar.make(
            fragment.requireView(), optionModel.titleResource, Snackbar.LENGTH_SHORT
        ).show()
    }


}