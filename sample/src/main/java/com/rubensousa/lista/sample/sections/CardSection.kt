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

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaViewHolder
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.databinding.SectionCardBinding
import com.rubensousa.lista.sample.model.CardModel

class CardSection(
    private val layout: Int = R.layout.section_card,
    private val showPosition: Boolean = true
) : ListaSection<CardModel, CardSection.ViewHolder>(itemViewType = layout) {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = inflate(parent, layout)
        return ViewHolder(SectionCardBinding.bind(view), showPosition)
    }

    class ViewHolder(
        private val binding: SectionCardBinding,
        private val showPosition: Boolean
    ) : ListaViewHolder<CardModel>(binding.root) {

        override fun onBound(item: CardModel, payloads: List<Any>) {
            super.onBound(item, payloads)
            itemView.tag = item.id
            binding.cardTextView.isVisible = showPosition
            binding.cardTextView.text = bindingAdapterPosition.toString()
        }

    }

}
