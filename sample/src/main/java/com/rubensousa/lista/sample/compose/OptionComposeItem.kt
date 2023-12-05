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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rubensousa.lista.compose.ListaLazyGridItem
import com.rubensousa.lista.sample.model.OptionModel
import com.rubensousa.lista.section.ListaArgs

class OptionComposeItem(
    private val model: OptionModel
) : ListaLazyGridItem {

    override fun getKey(): String = "Option${model.id}"

    override fun getContentType(): Any = OptionModel::class

    override fun getSpan(): LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(maxLineSpan) }

    override fun gridContent(args: ListaArgs): @Composable LazyGridItemScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillGridWidth(inheritedHorizontalPadding = 8.dp)
                .clickable {}
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(model.titleResource),
                style = MaterialTheme.typography.titleSmall
            )
            model.subtitleResource?.let { subtitle ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(subtitle),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

}
