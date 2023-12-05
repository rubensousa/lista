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

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rubensousa.lista.section.ListaArgs
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ListaGridColumn(
    items: ImmutableList<ListaLazyGridItem>,
    columns: GridCells,
    modifier: Modifier = Modifier,
    args: ListaArgs = ListaArgs.EMPTY,
    reverseLayout: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) {
        Arrangement.Top
    } else {
        Arrangement.Bottom
    },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    lazyLoadingState: LazyLoadingState = LazyLoadingState.Idle,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    onScrolled: ((lastVisiblePosition: Int) -> Unit)? = null,
    state: LazyGridState = rememberLazyGridState()
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = columns,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        state = state
    ) {
        items(
            items = items,
            key = { item -> item.getKey() },
            contentType = { item -> item.getContentType() },
            span = { item -> item.getSpan().invoke(this) }
        ) { item ->
            item.gridContent(args).invoke(this)
        }
        lazyLoadingState.content?.let { lazyLoadingContent ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                lazyLoadingContent.Content(Modifier.fillMaxWidth())
            }
        }
    }

    onScrolled?.let {
        val lastVisiblePosition = remember {
            derivedStateOf {
                state.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            }
        }
        lastVisiblePosition.value?.let { position ->
            LaunchedEffect(key1 = position) {
                onScrolled(position)
            }
        }
    }

}
