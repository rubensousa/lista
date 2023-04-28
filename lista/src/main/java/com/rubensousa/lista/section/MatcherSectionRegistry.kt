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
 * A [ListaSectionRegistry] that matches a certain [ListaSection]
 * based on an actual object instance via [SectionItemMatcher]
 *
 * [registerForInstance] will register a [ListaSection] for an object instance of type T
 *
 * [register] will register a [ListaSection]
 * for a [SectionItemMatcher] for maximum flexibility
 */
open class MatcherSectionRegistry<T> : ListaSectionRegistry<T>() {

    private val sectionMatchers = LinkedHashMap<SectionItemMatcher<T>, ListaSection<out T, *>>()

    override fun getSectionForItem(item: T?): ListaSection<out T, *>? {
        sectionMatchers.keys.forEach { matcher ->
            if (item != null && matcher.matches(item)) {
                return sectionMatchers[matcher]
            }
        }
        return null
    }

    inline fun <reified V : T> registerForInstance(section: ListaSection<out V, *>) {
        register(section, object : SectionItemMatcher<T> {
            override fun matches(item: T): Boolean {
                return item is V
            }
        })
    }

    fun register(section: ListaSection<out T, *>, matcher: (item: T) -> Boolean) {
        register(section, object : SectionItemMatcher<T> {
            override fun matches(item: T): Boolean {
                return matcher(item)
            }
        })
        registerSection(section)
    }

    fun register(section: ListaSection<out T, *>, matcher: SectionItemMatcher<T>) {
        sectionMatchers[matcher] = section
        registerSection(section)
    }

    interface SectionItemMatcher<T> {
        fun matches(item: T): Boolean
    }

}
