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

package io.cabriole.lista.sample.sections

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.cabriole.decorator.LinearMarginDecoration
import io.cabriole.lista.ListaAdapter
import io.cabriole.lista.ListaSectionViewHolder
import io.cabriole.lista.nested.ListaNestedSection
import io.cabriole.lista.nested.ListaNestedSectionViewHolder
import io.cabriole.lista.nested.ListaScrollStateManager
import io.cabriole.lista.sample.R
import io.cabriole.lista.sample.databinding.SectionCardListBinding
import io.cabriole.lista.sample.model.CardListModel

class CardListSection(
    recycledViewPool: RecyclerView.RecycledViewPool,
    scrollStateManager: ListaScrollStateManager
) : ListaNestedSection<CardListModel>(
    layoutId = R.layout.section_card_list,
    recycledViewPool = recycledViewPool,
    scrollStateManager = scrollStateManager
) {

    override fun onCreateViewHolder(view: View): ListaSectionViewHolder<CardListModel> {
        return VH(view, recycledViewPool, scrollStateManager)
    }

    override fun isForItem(item: Any): Boolean = item is CardListModel

    class VH(
        view: View,
        recycledViewPool: RecyclerView.RecycledViewPool,
        scrollStateManager: ListaScrollStateManager
    ) : ListaNestedSectionViewHolder<CardListModel>(view, recycledViewPool, scrollStateManager) {

        private val binding = SectionCardListBinding.bind(view)
        private val adapter = ListaAdapter(ListModelDiffCallback())

        override fun onCreated() {
            super.onCreated()
            adapter.addSection(CardSection())
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
            adapter.submitList(item.items, applyDiffing = false)
        }

        override fun onBind(item: CardListModel) {
            super.onBind(item)
            itemView.tag = item.id
        }

        override fun getRecyclerView(): RecyclerView = binding.cardRecyclerView

        override fun getAdapter(): RecyclerView.Adapter<*> = adapter

        override fun getScrollStateKey(item: CardListModel): String = item.getId()

        override fun recycleChildrenOnDetach(): Boolean = true

    }

}
