/*
 * Copyright 2023 Rúben Sousa
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

package com.rubensousa.lista.sample.recyclerview


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.decorator.LinearMarginDecoration
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.extensions.bindingOf
import com.rubensousa.lista.item.ListaItem
import com.rubensousa.lista.item.ListaItemAdapter
import com.rubensousa.lista.item.ListaItemSection
import com.rubensousa.lista.nested.ListaNestedViewHolder
import com.rubensousa.lista.nested.ListaScrollStateManager
import com.rubensousa.lista.pool.getActivityScopedRecycledViewPool
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.databinding.SectionCardListBinding
import com.rubensousa.lista.section.ListaArgs

class CardListRecyclerViewItem(override val model: CardListRecyclerViewModel) : ListaItem<CardListRecyclerViewModel> {

    override val diffId: String = model.id.toString()

    override fun createListaSection(
        args: ListaArgs
    ): ListaSection<ListaItem<CardListRecyclerViewModel>, *> {
        val scrollStateManager = args.require<ListaScrollStateManager>(ListaScrollStateManager.ARG_KEY)
        return ListaItemSection { parent ->
            ViewHolder(parent.bindingOf(SectionCardListBinding::inflate), scrollStateManager)
        }
    }

    class ViewHolder(
        private val binding: SectionCardListBinding,
        private val scrollStateManager: ListaScrollStateManager
    ) : ListaNestedViewHolder<ListaItem<CardListRecyclerViewModel>>(binding.root) {

        private val adapter = ListaItemAdapter()

        init {
            binding.cardRecyclerView.setRecycledViewPool(getActivityScopedRecycledViewPool())
        }

        override fun onCreated() {
            super.onCreated()
            scrollStateManager.setupRecyclerView(binding.cardRecyclerView)
            val layoutManager = LinearLayoutManager(
                itemView.context, RecyclerView.HORIZONTAL, false
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

        override fun onBound(item: ListaItem<CardListRecyclerViewModel>, payloads: List<Any>) {
            super.onBound(item, payloads)
            scrollStateManager.restoreScrollState(binding.cardRecyclerView, item.diffId)
            itemView.tag = item.diffId
        }

        override fun onRecycled() {
            scrollStateManager.saveScrollState(binding.cardRecyclerView)
            super.onRecycled()
        }

        override fun updateAdapter(item: ListaItem<CardListRecyclerViewModel>) {
            adapter.submitNow(item.model.items)
        }

        override fun getRecyclerView(): RecyclerView = binding.cardRecyclerView

        override fun getAdapter(): RecyclerView.Adapter<*> = adapter

    }

}
