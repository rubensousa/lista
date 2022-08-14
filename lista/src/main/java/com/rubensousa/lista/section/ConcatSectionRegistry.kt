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

package com.rubensousa.lista.section

import com.rubensousa.lista.ListaSection

/**
 * Similarly to ConcatAdapter, this [ConcatSectionRegistry] allows you
 * to register multiple [ListaSectionRegistry] to combine them.
 *
 * This is useful when you have different criteria to match certain items
 * or when you need a fallback to a certain position.
 *
 * Use [addRegistry] to register your individual [ListaSectionRegistry]
 *
 * Use [setFallback] to register a default section as fallback
 */
class ConcatSectionRegistry : ListaSectionRegistry() {

    private val registries = ArrayList<ListaSectionRegistry>()
    private var fallback: ListaSectionRegistry? = null

    fun addRegistry(registry: ListaSectionRegistry) {
        registries.add(registry)
    }

    fun setFallback(section: ListaSection<*, *>) {
        fallback = object : ListaSectionRegistry() {
            override fun <T> getSectionForItem(item: T): ListaSection<*, *> {
                return section
            }
        }
    }

    override fun <T> getSectionForItem(item: T): ListaSection<*, *>? {
        for (registry in registries) {
            return registry.getSectionForItem(item) ?: continue
        }
        return fallback?.getSectionForItem(item)
    }

    override fun getSectionForItemViewType(itemViewType: Int): ListaSection<*, *>? {
        for (registry in registries) {
            return registry.getSectionForItemViewType(itemViewType) ?: continue
        }
        return fallback?.getSectionForItemViewType(itemViewType)
    }

}
