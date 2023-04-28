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

package com.rubensousa.lista

import android.view.ViewGroup
import com.google.common.truth.Truth.assertThat
import com.rubensousa.lista.fakes.IntegerSection
import com.rubensousa.lista.fakes.StringSection
import com.rubensousa.lista.fakes.TestViewHolder
import com.rubensousa.lista.section.ClassSectionRegistry
import com.rubensousa.lista.section.ConcatSectionRegistry
import com.rubensousa.lista.section.MatcherSectionRegistry
import org.junit.Test

class SectionRegistryTest {

    @Test
    fun `unique itemViewType is generated`() {
        val registry = ClassSectionRegistry<Any>()
        val stringSection = StringSection()
        val integerSection = IntegerSection()
        registry.register(stringSection)
        registry.register(integerSection)

        assertThat(stringSection.getItemViewType()).isEqualTo(-1)
        assertThat(integerSection.getItemViewType()).isEqualTo(-2)
    }

    @Test
    fun `section is found from class instance`() {
        val registry = ClassSectionRegistry<Any>()
        val section = StringSection()
        registry.register(section)

        assertThat(registry.getSectionForItem("Test")).isSameInstanceAs(section)
        assertThat(registry.getSectionForItemViewType(section.getItemViewType()))
            .isEqualTo(section)
    }

    @Test
    fun `section is found from item matcher`() {
        val registry = MatcherSectionRegistry<Any>()
        val firstSection = StringSection()
        val secondSection = StringSection()
        registry.register(firstSection) { item ->
            item is String && item.length < 2
        }
        registry.register(secondSection) { item ->
            item is String && item.length >= 2
        }
        assertThat(registry.getSectionForItem("A")).isSameInstanceAs(firstSection)
        assertThat(registry.getSectionForItem("AB")).isSameInstanceAs(secondSection)
    }

    @Test
    fun `placeholder section is found from ConcatSectionRegistry`() {
        val registry = ConcatSectionRegistry()

        val classRegistry = ClassSectionRegistry<Any>()
        val stringSection = StringSection()
        classRegistry.register(stringSection)
        registry.addRegistry(classRegistry)

        val placeholderSection = PlaceholderSection()
        registry.setFallback(placeholderSection)

        assertThat(registry.getSectionForItem("A")).isSameInstanceAs(stringSection)
        assertThat(registry.getSectionForItem(0)).isSameInstanceAs(placeholderSection)
        assertThat(registry.getSectionForItemViewType(PlaceholderSection.ITEM_VIEW_TYPE))
            .isSameInstanceAs(placeholderSection)

    }

    class PlaceholderSection : ListaSection<Boolean, TestViewHolder<Boolean>>(ITEM_VIEW_TYPE) {

        companion object {
            const val ITEM_VIEW_TYPE = 1400
        }

        override fun onCreateViewHolder(parent: ViewGroup): TestViewHolder<Boolean> {
            return TestViewHolder.create()
        }

    }

}

