/*
 * Copyright 2022 Rúben Sousa
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

package com.rubensousa.lista.sample.ui

import androidx.core.view.isVisible
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaViewHolder
import com.rubensousa.lista.extensions.bindingOf
import com.rubensousa.lista.item.ListaItem
import com.rubensousa.lista.item.ListaItemSection
import com.rubensousa.lista.sample.databinding.SectionCardBinding
import com.rubensousa.lista.sample.model.CardModel
import com.rubensousa.lista.section.ListaArgs

class SmallCardItem(override val model: CardModel) : ListaItem<CardModel> {

    override val diffId: String = "Card${model.id}"

    override fun createListaSection(args: ListaArgs): ListaSection<ListaItem<CardModel>, *> {
        return ListaItemSection(itemViewType = ItemViewTypes.CARD_LIST_ITEM) { parent ->
            ViewHolder(parent.bindingOf(SectionCardBinding::inflate), showPosition = true)
        }
    }

    class ViewHolder(
        private val binding: SectionCardBinding,
        private val showPosition: Boolean
    ) : ListaViewHolder<ListaItem<CardModel>>(binding.root) {

        override fun onBound(item: ListaItem<CardModel>, payloads: List<Any>) {
            itemView.tag = item.model.id
            binding.cardTextView.isVisible = showPosition
            binding.cardTextView.text = bindingAdapterPosition.toString()
        }

    }

}