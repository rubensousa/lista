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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rubensousa.lista.compose.ListaLazyGridItem
import com.rubensousa.lista.sample.model.HeaderModel
import com.rubensousa.lista.section.ListaArgs

class HeaderComposeItem(private val model: HeaderModel) : ListaLazyGridItem {

    override fun getKey(): String = model.getId()

    override fun getSpan(): LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

    override fun gridContent(args: ListaArgs): @Composable LazyGridItemScope.() -> Unit = {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(model.titleResource),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )
    }

}
