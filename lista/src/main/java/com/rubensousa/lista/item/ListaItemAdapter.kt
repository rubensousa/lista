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
import com.rubensousa.lista.section.ItemSectionRegistry
import com.rubensousa.lista.section.ListaSectionArgs

class ListaItemAdapter<T : Any>(
    args: ListaSectionArgs = ListaSectionArgs.EMPTY,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListaAdapter<ListaItem<T>>(object : DiffUtil.ItemCallback<ListaItem<T>>() {
    override fun areItemsTheSame(oldItem: ListaItem<T>, newItem: ListaItem<T>): Boolean {
        return diffCallback.areItemsTheSame(oldItem.model, newItem.model)
    }
    override fun areContentsTheSame(oldItem: ListaItem<T>, newItem: ListaItem<T>): Boolean {
        return diffCallback.areContentsTheSame(oldItem.model, newItem.model)
    }
}) {

    init {
        setSectionRegistry(ItemSectionRegistry(args))
    }

}
