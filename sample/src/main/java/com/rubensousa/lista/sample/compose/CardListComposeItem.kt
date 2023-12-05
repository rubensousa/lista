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

package com.rubensousa.lista.sample.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rubensousa.lista.compose.ListaItemRow
import com.rubensousa.lista.compose.ListaLazyGridItem
import com.rubensousa.lista.section.ListaArgs

class CardListComposeItem(private val model: CardListComposeModel) : ListaLazyGridItem {

    override fun getKey(): String = "CardList${model.id}"

    override fun getContentType(): Any = CardListComposeModel::class

    override fun getSpan(): LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

    override fun gridContent(args: ListaArgs): @Composable LazyGridItemScope.() -> Unit = {
        ListaItemRow(
            modifier = Modifier
                .fillGridWidth(inheritedHorizontalPadding = 8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            items = model.items
        )
    }

}
