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
 * based on the class of the item bound.
 *
 * Use [register] to add a [ListaSection] bound to the its type.
 *
 * Use [registerForClass] or [registerForClass]
 */
class ClassSectionRegistry : ListaSectionRegistry {

    private val sections = LinkedHashMap<Class<*>, ListaSection<*>>()
    private val sectionsPerViewType = LinkedHashMap<Int, ListaSection<*>>()

    fun registerForClass(section: ListaSection<*>, clazz: Class<*>) {
        sections[clazz] = section
        sectionsPerViewType[section.getItemViewType()] = section
    }

    inline fun <reified T> register(section: ListaSection<T>) {
        registerForClass(section, T::class.java)
    }

    override fun getSectionForItem(item: Any): ListaSection<*>? {
        return sections[item::class.java]
    }

    override fun getSectionForItemViewType(itemViewType: Int): ListaSection<*>? {
        return sectionsPerViewType[itemViewType]
    }

}
