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

import androidx.annotation.NonNull
import com.rubensousa.lista.ListaSection
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * A [ListaSectionRegistry] is responsible for finding a suitable [ListaSection]
 * for a certain object and itemViewType
 *
 * Call [registerSection] in your child classes to register your section
 *
 * Check [ClassSectionRegistry] and [ItemSectionRegistry] for some default implementations.
 */
abstract class ListaSectionRegistry {

    private val sectionsPerViewType = LinkedHashMap<Int, ListaSection<*, *>>()

    abstract fun <T> getSectionForItem(@NonNull item: T): ListaSection<*, *>?

    open fun getSectionForItemViewType(itemViewType: Int): ListaSection<*, *>? {
        return sectionsPerViewType[itemViewType]
    }

    open fun getSections(): List<ListaSection<*, *>> {
        if (sectionsPerViewType.isEmpty()) {
            return Collections.emptyList()
        }
        return sectionsPerViewType.values.toList()
    }

    protected fun registerSection(section: ListaSection<*, *>) {
        if (section.getItemViewType() == ListaSection.VIEW_TYPE_AUTO_GENERATED) {
            // Set the id starting from negative numbers
            // to avoid any collision from ids set by the user
            section.setGeneratedItemViewType(-sectionsPerViewType.size - 1)
        }
        sectionsPerViewType[section.getItemViewType()] = section
    }

}
