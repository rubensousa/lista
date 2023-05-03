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

import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.section.ListaSectionArgs

interface ListaItem<out T: Any> {

    val model: T

    val diffId: String

    fun createListaSection(args: ListaSectionArgs): ListaSection<ListaItem<@UnsafeVariance T>, *>

    /**
     * @return null for full span count, or >= 1 for one specific span size
     */
    fun getSpanSize(): Int? = null

    fun areItemsTheSame(other: ListaItem<Any>): Boolean = diffId == other.diffId

    fun areContentsTheSame(other: ListaItem<Any>): Boolean = model == other.model

}
