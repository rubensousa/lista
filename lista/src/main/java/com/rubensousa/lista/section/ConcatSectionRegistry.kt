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
class ConcatSectionRegistry : ListaSectionRegistry<Any>() {

    private val registries = ArrayList<ListaSectionRegistry<Any>>()
    private var fallback: ListaSectionRegistry<Any>? = null

    override fun getSectionForItem(item: Any?): ListaSection<out Any, *>? {
        for (registry in registries) {
            return registry.getSectionForItem(item) ?: continue
        }
        return fallback?.getSectionForItem(item)
    }

    override fun getSectionForItemViewType(itemViewType: Int): ListaSection<out Any, *>? {
        for (registry in registries) {
            return registry.getSectionForItemViewType(itemViewType) ?: continue
        }
        return fallback?.getSectionForItemViewType(itemViewType)
    }

    override fun getSections(): List<ListaSection<out Any, *>> {
        val sections = ArrayList<ListaSection<out Any, *>>()
        registries.forEach { registry ->
            sections.addAll(registry.getSections())
        }
        return sections
    }

    fun addRegistry(registry: ListaSectionRegistry<Any>) {
        registries.add(registry)
    }

    fun setFallback(section: ListaSection<out Any, *>) {
        fallback = FallbackSectionRegistry(section)
    }

    private class FallbackSectionRegistry<T>(private val section: ListaSection<out T, *>) :
        ListaSectionRegistry<T>() {

        override fun getSections(): List<ListaSection<out T, *>> = listOf(section)

        override fun getSectionForItemViewType(itemViewType: Int): ListaSection<out T, *> = section

        override fun getSectionForItem(item: T?): ListaSection<out T, *> = section

    }

}
