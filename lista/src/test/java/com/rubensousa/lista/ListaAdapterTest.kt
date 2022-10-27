/*
 * Copyright (c) 2022. Rúben Sousa
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


import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import com.rubensousa.lista.fakes.IntegerSection
import com.rubensousa.lista.fakes.StringSection
import com.rubensousa.lista.fakes.TestViewHolder
import com.rubensousa.lista.section.ClassSectionRegistry
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class ListaAdapterTest {

    private val diffItemCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
    private val mainThreadExecutor = TestExecutor()
    private val backgroundExecutor = TestExecutor()
    private lateinit var adapter: ListaAdapter<Any>

    @Before
    fun setup() {
        adapter = ListaAdapter(
            differConfig = ListaAsyncDiffer.Config.buildTest(
                diffItemCallback, mainThreadExecutor, backgroundExecutor
            ),
            updateCallback = object : ListaAsyncDiffer.UpdateCallback {
                override fun onInvalidated() {}

                override fun onInserted(position: Int, count: Int) {}

                override fun onRemoved(position: Int, count: Int) {}

                override fun onMoved(fromPosition: Int, toPosition: Int) {}

                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            }
        )
    }

    @Test
    fun `adapter returns itemViewTypes from sections`() {
        val integerSection = IntegerSection()
        val stringSection = StringSection()
        adapter.setSectionRegistry(ClassSectionRegistry().apply {
            register(integerSection)
            register(stringSection)
        })
        adapter.submitNow(listOf(1, "Test"))

        assertThat(adapter.getItemViewType(0)).isEqualTo(integerSection.getItemViewType())
        assertThat(adapter.getItemViewType(1)).isEqualTo(stringSection.getItemViewType())
        assertThat(integerSection.getItemViewType()).isNotEqualTo(stringSection.getItemViewType())
    }

    @Test
    fun `adapter item count is the same as size of list`() {
        assertThat(adapter.itemCount).isEqualTo(0)
        adapter.submitNow(listOf(1, "Test"))
        assertThat(adapter.itemCount).isEqualTo(2)
    }

    @Test
    fun `clear will erase the entire list`() {
        adapter.submitNow(listOf(1, "Test"))
        assertThat(adapter.itemCount).isEqualTo(2)

        adapter.clear()

        backgroundExecutor.executeAll()
        assertThat(adapter.itemCount).isEqualTo(2)

        mainThreadExecutor.executeAll()
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun `clear now will erase the entire list immediately`() {
        adapter.submitNow(listOf(1, "Test"))
        assertThat(adapter.itemCount).isEqualTo(2)

        adapter.clearNow()

        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun `submitting nulls works fine`() {
        adapter.submit(listOf(null, null, null))

        backgroundExecutor.executeAll()
        mainThreadExecutor.executeAll()

        assertThat(adapter.getItemAt(0)).isNull()
        assertThat(adapter.getItemAt(1)).isNull()
        assertThat(adapter.getItemAt(2)).isNull()

    }

    @Test
    fun `adapter returns same instance of section registry that was set`() {
        val integerSection = IntegerSection()
        val stringSection = StringSection()

        val sectionRegistry = ClassSectionRegistry().apply {
            register(integerSection)
            register(stringSection)
        }

        adapter.setSectionRegistry(sectionRegistry)

        assertThat(adapter.getSectionRegistry()).isSameInstanceAs(sectionRegistry)
    }

    @Test(expected = IllegalStateException::class)
    fun `exception is thrown when adapter does not have registered sections`() {
        adapter.submitNow(listOf(1, 2, 3))
        adapter.getItemViewType(0)
    }

    @Test
    fun `adapter forwards events to the sections`() {
        val itemViewType = 4
        adapter.setSectionRegistry(ClassSectionRegistry().apply {
            register(IntegerSection(itemViewType = itemViewType))
        })
        adapter.submitNow(listOf(0, 1, 2))

        val viewHolder = adapter.onCreateViewHolder(mockk(), itemViewType) as TestViewHolder
        setViewType(viewHolder, itemViewType)

        assertThat(viewHolder.createdCalls).isEqualTo(1)
        assertThat(viewHolder.bindCalls).isEqualTo(0)

        adapter.onBindViewHolder(viewHolder, 0)

        assertThat(viewHolder.bindCalls).isEqualTo(1)
        assertThat(viewHolder.payloads).isEmpty()
        assertThat(viewHolder.getItem()).isEqualTo(0)

        adapter.onViewDetachedFromWindow(viewHolder)
        assertThat(viewHolder.detachedFromWindowCalls).isEqualTo(1)

        adapter.onViewAttachedToWindow(viewHolder)
        assertThat(viewHolder.attachedToWindowCalls).isEqualTo(1)

        adapter.onViewRecycled(viewHolder)
        assertThat(viewHolder.recycledCalls).isEqualTo(1)
        assertThat(viewHolder.getItem()).isNull()

        val payload = listOf(2)
        adapter.onBindViewHolder(viewHolder, 1, payload)
        assertThat(viewHolder.bindCalls).isEqualTo(2)
        assertThat(viewHolder.payloads).isEqualTo(payload)
        assertThat(viewHolder.getItem()).isEqualTo(1)

        adapter.onFailedToRecycleView(viewHolder)
        assertThat(viewHolder.failedToRecycleCalls).isEqualTo(1)
    }

    // Since we're not using a real RecyclerView, we need to set the itemViewType ourselves
    private fun setViewType(viewHolder: RecyclerView.ViewHolder, itemViewType: Int) {
        val field = viewHolder::class.java.superclass!!.superclass!!
            .getDeclaredField("mItemViewType")
        field.isAccessible = true
        field.setInt(viewHolder, itemViewType)
    }

}
