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

package com.rubensousa.lista.sample.sections

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.decorator.LinearMarginDecoration
import com.rubensousa.lista.ListaAdapter
import com.rubensousa.lista.nested.ListaNestedSection
import com.rubensousa.lista.nested.ListaNestedSectionViewHolder
import com.rubensousa.lista.nested.ListaScrollStateManager
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.databinding.SectionCardListBinding
import com.rubensousa.lista.sample.model.CardListModel
import com.rubensousa.lista.section.ItemSectionRegistry

class CardListSection(
    recycledViewPool: RecyclerView.RecycledViewPool,
    scrollStateManager: ListaScrollStateManager
) : ListaNestedSection<CardListModel, CardListSection.VH>(
    layoutId = R.layout.section_card_list,
    recycledViewPool = recycledViewPool,
    scrollStateManager = scrollStateManager
) {

    override fun onCreateViewHolder(view: View): VH {
        return VH(view, recycledViewPool, scrollStateManager)
    }

    class VH(
        view: View,
        recycledViewPool: RecyclerView.RecycledViewPool,
        scrollStateManager: ListaScrollStateManager
    ) : ListaNestedSectionViewHolder<CardListModel>(view, recycledViewPool, scrollStateManager) {

        private val binding = SectionCardListBinding.bind(view)
        private val adapter = ListaAdapter(ListModelDiffCallback())

        override fun onCreated() {
            super.onCreated()
            adapter.setSectionRegistry(ItemSectionRegistry().apply {
                registerForInstance(CardSection())
            })
            val layoutManager = LinearLayoutManager(
                binding.cardRecyclerView.context, RecyclerView.HORIZONTAL, false
            )
            layoutManager.recycleChildrenOnDetach = true
            binding.cardRecyclerView.layoutManager = layoutManager
            binding.cardRecyclerView.adapter = adapter
            binding.cardRecyclerView.addItemDecoration(
                LinearMarginDecoration(
                    leftMargin = itemView.resources.getDimensionPixelOffset(
                        R.dimen.default_decoration_size
                    ),
                    rightMargin = itemView.resources.getDimensionPixelOffset(
                        R.dimen.default_decoration_size
                    ),
                    orientation = RecyclerView.HORIZONTAL
                )
            )
        }

        override fun updateAdapter(item: CardListModel) {
            adapter.submitNow(item.items)
        }

        override fun onBind(item: CardListModel, payloads: List<Any>) {
            super.onBind(item, payloads)
            itemView.tag = item.id
        }

        override fun getRecyclerView(): RecyclerView = binding.cardRecyclerView

        override fun getAdapter(): RecyclerView.Adapter<*> = adapter

        override fun getScrollStateKey(item: CardListModel): String = item.getId()

        override fun recycleChildrenOnDetach(): Boolean = true

    }

}
