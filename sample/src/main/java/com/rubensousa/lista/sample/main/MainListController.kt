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

package com.rubensousa.lista.sample.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.GridSpanMarginDecoration
import com.rubensousa.decorator.LinearBoundsMarginDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import com.rubensousa.lista.ListaAdapter
import com.rubensousa.lista.ListaController
import com.rubensousa.lista.ListaSpanLookup
import com.rubensousa.lista.nested.ListaScrollStateManager
import com.rubensousa.lista.pool.getActivityScopedRecycledViewPool
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.model.CardListModel
import com.rubensousa.lista.sample.model.CardModel
import com.rubensousa.lista.sample.model.OptionModel
import com.rubensousa.lista.sample.model.SectionModel
import com.rubensousa.lista.sample.sections.*
import com.rubensousa.lista.section.ClassSectionRegistry
import com.rubensousa.lista.section.ListaSectionRegistry

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

    override fun getRecycledViewPool(): RecyclerView.RecycledViewPool {
        return fragment.getActivityScopedRecycledViewPool()
    }

    override fun createSectionRegistry(
        adapter: ListaAdapter<SectionModel>,
        recyclerView: RecyclerView
    ): ListaSectionRegistry {
        val registry = ClassSectionRegistry()
        val cardSection = CardSection(
            layout = R.layout.section_card_grid,
            showPosition = false
        )
        registry.register(OptionSection(this))
        registry.register(HeaderSection())
        registry.register(CardListSection(scrollStateManager))
        registry.register(cardSection)

        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val spanSizeLookup = ListaSpanLookup(adapter, defaultSpanSize = layoutManager.spanCount)
        spanSizeLookup.setSpanSizeForSection(cardSection, 1)
        layoutManager.spanSizeLookup = spanSizeLookup

        return registry
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
            GridSpanMarginDecoration.createHorizontal(
                horizontalMargin = defaultDecorationSize,
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