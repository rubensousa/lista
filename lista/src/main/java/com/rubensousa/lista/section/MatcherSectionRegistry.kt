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

typealias SectionItemMatcher = (item: Any) -> Boolean

/**
 * A [ListaSectionRegistry] that matches a certain [ListaSection]
 * based on an actual object instance via [SectionItemMatcher]
 *
 * [registerForInstance] will register a [ListaSection] for an object instance of type T
 *
 * [registerForMatcher] will register a [ListaSection]
 * for a [SectionItemMatcher] for maximum flexibility
 */
open class MatcherSectionRegistry : ListaSectionRegistry() {

    private val sectionMatchers = LinkedHashMap<SectionItemMatcher, ListaSection<*, *>>()

    inline fun <reified T> registerForInstance(section: ListaSection<T, *>) {
        registerForMatcher(section) { item -> item is T }
    }

    fun registerForMatcher(section: ListaSection<*, *>, itemMatcher: SectionItemMatcher) {
        sectionMatchers[itemMatcher] = section
        registerSection(section)
    }

    override fun <T> getSectionForItem(item: T): ListaSection<*, *>? {
        sectionMatchers.keys.forEach { matcher ->
            if (matcher(item as Any)) {
                return sectionMatchers[matcher]
            }
        }
        return null
    }

}
