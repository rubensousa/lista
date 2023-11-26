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

package com.rubensousa.lista.item

import androidx.recyclerview.widget.DiffUtil
import com.rubensousa.lista.ListaAdapter
import com.rubensousa.lista.section.ListaArgs

class ListaItemAdapter(
    args: ListaArgs = ListaArgs.EMPTY,
) : ListaAdapter<ListaItem<Any>>(object : DiffUtil.ItemCallback<ListaItem<Any>>() {
    override fun areItemsTheSame(oldItem: ListaItem<Any>, newItem: ListaItem<Any>): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }
    override fun areContentsTheSame(oldItem: ListaItem<Any>, newItem: ListaItem<Any>): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}) {

    init {
        setSectionRegistry(ItemSectionRegistry(args))
    }

}
