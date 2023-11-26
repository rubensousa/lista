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

package com.rubensousa.lista.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubensousa.lista.compose.ListaLazyGridItem
import com.rubensousa.lista.sample.compose.BigCardComposeItem
import com.rubensousa.lista.sample.compose.HeaderComposeItem
import com.rubensousa.lista.sample.compose.OptionComposeItem
import com.rubensousa.lista.sample.compose.SmallCardComposeItem
import com.rubensousa.lista.sample.model.*
import com.rubensousa.lista.sample.recyclerview.BigCardRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.CardListRecyclerViewModel
import com.rubensousa.lista.sample.recyclerview.SmallCardRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.CardListRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.HeaderRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.OptionRecyclerViewItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ComposeViewModel : ViewModel() {

    private val listItems = MutableStateFlow<ImmutableList<ListaLazyGridItem>>(persistentListOf())

    init {
        viewModelScope.launch {
            loadItems()
        }
    }

    fun getListItems() = listItems.asStateFlow()

    private fun loadItems() {
        val list = arrayListOf<ListaLazyGridItem>()
        val headers = listOf(
            R.string.header_one, R.string.header_two, R.string.header_three,
            R.string.header_four, R.string.header_five, R.string.header_six
        )
        val cards = arrayListOf<SmallCardComposeItem>()
        repeat(20) {
            cards.add(SmallCardComposeItem(CardModel(it)))
        }
        headers.forEach { headerTitle ->
            list.add(HeaderComposeItem(HeaderModel(titleResource = headerTitle)))
            list.add(
                OptionComposeItem(
                    OptionModel(
                        id = list.size,
                        titleResource = R.string.option_one,
                        subtitleResource = R.string.option_subtitle
                    )
                )
            )
            list.add(BigCardComposeItem(CardModel(list.size)))
            list.add(BigCardComposeItem(CardModel(list.size)))
            list.add(
                OptionComposeItem(
                    OptionModel(
                        id = list.size,
                        titleResource = R.string.option_two,
                        subtitleResource = R.string.option_subtitle
                    )
                )
            )
           // list.add(CardListRecyclerViewItem(CardListRecyclerViewModel(id = list.size, items = ArrayList(cards))))
        }
        listItems.value = list.toImmutableList()
    }

}