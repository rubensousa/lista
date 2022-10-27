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
import com.rubensousa.lista.ListaViewHolder

/**
 * A [ListaSectionRegistry] that matches a certain [ListaSection]
 * based on the class of the item bound.
 *
 * Use [register] to add a [ListaSection] bound to the its type.
 *
 */
open class ClassSectionRegistry : ListaSectionRegistry() {

    private val sections = LinkedHashMap<Class<*>, ListaSection<*, *>>()

    inline fun <reified T, VH: ListaViewHolder<T>> register(section: ListaSection<T, VH>) {
        registerForClass(section, T::class.java)
    }

    fun registerForClass(section: ListaSection<*, *>, clazz: Class<*>) {
        sections[clazz] = section
        registerSection(section)
    }

    override fun <T> getSectionForItem(item: T): ListaSection<*, *>? {
        return sections[item!!::class.java]
    }

}
