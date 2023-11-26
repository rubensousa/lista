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

package com.rubensousa.lista.compose

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset
import com.rubensousa.lista.section.ListaArgs
import kotlin.math.roundToInt

@Immutable
interface ListaLazyGridItem {

    companion object {
        private val DEFAULT_SPAN = GridItemSpan(1)
    }

    fun getKey(): String

    fun getContentType(): Any? = null

    fun getSpan(): LazyGridItemSpanScope.() -> GridItemSpan = { DEFAULT_SPAN }

    fun content(args: ListaArgs): @Composable LazyGridItemScope.() -> Unit

    /**
     * Adjusts the bounds of an item that should fill the entire grid
     */
    fun Modifier.fillGridWidth(inheritedHorizontalPadding: Dp) = then(
        layout { measurable, constraints ->
            val paddingPixels = (inheritedHorizontalPadding.value * density).roundToInt()
            val targetConstraints = constraints.offset(paddingPixels * 2, 0)
            val placeable = measurable.measure(targetConstraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    )
}
