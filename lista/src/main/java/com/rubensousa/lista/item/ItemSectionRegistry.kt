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
import com.rubensousa.lista.section.ListaArgs
import com.rubensousa.lista.section.ListaSectionRegistry

internal class ItemSectionRegistry(
    private val args: ListaArgs
) : ListaSectionRegistry<ListaItem<Any>>() {

    private val classSections = LinkedHashMap<Class<*>, ListaSection<out ListaItem<Any>, *>>()

    override fun getSectionForItem(item: ListaItem<Any>?): ListaSection<out ListaItem<Any>, *>? {
        val currentItem = item ?: return null
        val currentSection = classSections[currentItem::class.java]
        if (currentSection != null) return currentSection
        val newSection = currentItem.createListaSection(args)
        classSections[currentItem::class.java] = newSection
        registerSection(newSection)
        return newSection
    }

}
