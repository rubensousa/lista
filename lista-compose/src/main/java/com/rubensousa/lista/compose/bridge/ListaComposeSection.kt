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

package com.rubensousa.lista.compose.bridge

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import com.rubensousa.lista.ListaSection

class ListaComposeSection<T>(
    private val listener: ViewHolderEventListener<T>? = null,
    private val content: @Composable (item: T) -> Unit,
) : ListaSection<T, ListaComposeViewHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup): ListaComposeViewHolder<T> {
        return ListaComposeViewHolder(parent, content)
    }

    override fun onViewHolderBound(holder: ListaComposeViewHolder<T>, item: T, payloads: List<Any>) {
        super.onViewHolderBound(holder, item, payloads)
        listener?.onViewHolderBound(holder, item)
    }

    override fun onViewHolderRecycled(holder: ListaComposeViewHolder<T>) {
        super.onViewHolderRecycled(holder)
        listener?.onViewHolderRecycled(holder)
    }

    interface ViewHolderEventListener<T> {
        fun onViewHolderBound(holder: ListaComposeViewHolder<T>, item: T)
        fun onViewHolderRecycled(holder: ListaComposeViewHolder<*>){}
    }

}
