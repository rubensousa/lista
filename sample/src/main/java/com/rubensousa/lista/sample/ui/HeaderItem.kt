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

package com.rubensousa.lista.sample.ui

import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaViewHolder
import com.rubensousa.lista.extensions.bindingOf
import com.rubensousa.lista.item.ListaItem
import com.rubensousa.lista.item.ListaItemSection
import com.rubensousa.lista.sample.databinding.SectionHeaderBinding
import com.rubensousa.lista.sample.model.HeaderModel
import com.rubensousa.lista.section.ListaSectionArgs

class HeaderItem(override val model: HeaderModel) : ListaItem<HeaderModel> {

    override val diffId: String = model.getId()

    override fun createListaSection(
        args: ListaSectionArgs
    ): ListaSection<ListaItem<HeaderModel>, *> {
        return ListaItemSection { parent ->
            ViewHolder(parent.bindingOf(SectionHeaderBinding::inflate))
        }
    }

    private class ViewHolder(
        private val binding: SectionHeaderBinding
    ) : ListaViewHolder<ListaItem<HeaderModel>>(binding.root) {

        override fun onBound(item: ListaItem<HeaderModel>, payloads: List<Any>) {
            super.onBound(item, payloads)
            binding.headerTitleTextView.setText(item.model.titleResource)
        }

    }

}
