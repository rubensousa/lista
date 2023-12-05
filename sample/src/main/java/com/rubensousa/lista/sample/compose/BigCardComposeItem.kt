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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.rubensousa.lista.compose.ListaLazyGridItem
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.model.CardModel
import com.rubensousa.lista.section.ListaArgs

class BigCardComposeItem(private val model: CardModel) : ListaLazyGridItem {

    override fun getKey(): String = "Card${model.id}"

    override fun getContentType(): Any = "BigCard"

    override fun gridContent(args: ListaArgs): @Composable LazyGridItemScope.() -> Unit = {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.colorPrimary)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {

        }
    }

}