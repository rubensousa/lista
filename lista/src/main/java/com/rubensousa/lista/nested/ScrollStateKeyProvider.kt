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

package com.rubensousa.lista.nested

import com.rubensousa.lista.item.ListaItem

interface ScrollStateKeyProvider<T> {

    companion object {
        val DEFAULT = object: ScrollStateKeyProvider<ListaItem<Any>> {
            override fun getKey(item: ListaItem<Any>): String = item.diffId
        }
    }

    fun getKey(item: T): String

}
