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
import com.rubensousa.lista.section.ItemSectionRegistry
import org.junit.Test

class SectionRegistryTest {

    @Test
    fun `unique itemViewType is generated`() {
        val registry = ClassSectionRegistry()
        val stringSection = StringSection()
        val integerSection = IntegerSection()
        registry.register(stringSection)
        registry.register(integerSection)

        assertThat(stringSection.getItemViewType()).isEqualTo(Int.MAX_VALUE)
        assertThat(integerSection.getItemViewType()).isEqualTo(Int.MAX_VALUE - 1)
    }

    @Test
    fun `section is found from class instance`() {
        val registry = ClassSectionRegistry()
        val section = StringSection()
        registry.register(section)

        assertThat(registry.getSectionForItem("Test")).isSameInstanceAs(section)
        assertThat(registry.getSectionForItemViewType(section.getItemViewType()))
            .isEqualTo(section)
    }

    @Test
    fun `section is found from item matcher`() {
        val registry = ItemSectionRegistry()
        val firstSection = StringSection()
        val secondSection = StringSection()
        registry.registerForMatcher(firstSection) { item ->
            item is String && item.length < 2
        }
        registry.registerForMatcher(secondSection) { item ->
            item is String && item.length >= 2
        }
        assertThat(registry.getSectionForItem("A")).isSameInstanceAs(firstSection)
        assertThat(registry.getSectionForItem("AB")).isSameInstanceAs(secondSection)
    }

    @Test
    fun `placeholder section is found from ConcatSectionRegistry`() {
        val registry = ConcatSectionRegistry()

        val classRegistry = ClassSectionRegistry()
        val stringSection = StringSection()
        classRegistry.register(stringSection)
        registry.addRegistry(classRegistry)

        val placeholderSection = PlaceholderSection()
        registry.setFallback(placeholderSection)

        assertThat(registry.getSectionForItem("A")).isSameInstanceAs(stringSection)
        assertThat(registry.getSectionForItem(0)).isSameInstanceAs(placeholderSection)
    }

    class PlaceholderSection : ListaSection<Boolean, TestViewHolder<Boolean>>() {
        override fun onCreateViewHolder(parent: ViewGroup): TestViewHolder<Boolean> {
            return TestViewHolder.create()
        }
    }

}
