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
import com.rubensousa.lista.sample.databinding.SectionOptionBinding
import com.rubensousa.lista.sample.model.OptionModel
import com.rubensousa.lista.section.ListaArgs

class OptionItem(
    override val model: OptionModel,
    private val onClick: () -> Unit
) : ListaItem<OptionModel> {

    override val diffId: String = "Option${model.id}"

    override fun createListaSection(
        args: ListaArgs
    ): ListaSection<ListaItem<OptionModel>, *> {
        return ListaItemSection(
            onCreated = { holder ->
                holder.itemView.setOnClickListener {
                    onClick()
                }
            },
            viewHolderCreator = { parent ->
                ViewHolder(parent.bindingOf(SectionOptionBinding::inflate))
            })
    }

    private class ViewHolder(
        private val binding: SectionOptionBinding
    ) : ListaViewHolder<ListaItem<OptionModel>>(binding.root) {

        override fun onBound(item: ListaItem<OptionModel>, payloads: List<Any>) {
            val model = item.model
            binding.optionTitleTextView.setText(model.titleResource)
            if (model.subtitleResource == null) {
                binding.optionSubtitleTextView.text = ""
                binding.optionSubtitleTextView.isVisible = false
            } else {
                binding.optionSubtitleTextView.isVisible = true
                binding.optionSubtitleTextView.setText(model.subtitleResource)
            }
        }

    }

}
