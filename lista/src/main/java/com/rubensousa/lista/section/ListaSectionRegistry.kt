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
 * A [ListaSectionRegistry] is responsible for finding a suitable [ListaSection]
 * for a certain object or itemViewType
 *
 * Check [ClassSectionRegistry] and [ItemSectionRegistry] for some default implementations
 */
interface ListaSectionRegistry {
    fun getSectionForItem(item: Any): ListaSection<*>?
    fun getSectionForItemViewType(itemViewType: Int): ListaSection<*>?
}