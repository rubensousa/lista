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

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import com.rubensousa.lista.fakes.FakeIntegerSection
import com.rubensousa.lista.fakes.FakeStringSection
import com.rubensousa.lista.fakes.FakeViewHolder
import com.rubensousa.lista.section.ClassSectionRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.lang.IllegalStateException

@RunWith(MockitoJUnitRunner::class)
class ListaAdapterTest {

    @Mock
    lateinit var fakeView: View

    @Mock
    lateinit var fakeViewGroup: ViewGroup

    private val diffItemCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
    private lateinit var adapter: ListaAdapter<Any>

    @Before
    fun setup() {
        adapter = ListaAdapter(diffItemCallback)
    }

    @Test
    fun `adapter returns itemViewTypes from sections`() {
        val integerSection = FakeIntegerSection(fakeView, layoutId = 4)
        val stringSection = FakeStringSection(fakeView, layoutId = 5)
        adapter.setSectionRegistry(ClassSectionRegistry().apply {
            register(integerSection)
            register(stringSection)
        })
        adapter.setList(listOf(1, "Test"))

        assertThat(adapter.getItemViewType(0)).isEqualTo(integerSection.getItemViewType())
        assertThat(adapter.getItemViewType(1)).isEqualTo(stringSection.getItemViewType())
    }

    @Test(expected = IllegalStateException::class)
    fun `exception is thrown when adapter does not have registered sections`() {
        val adapter = ListaAdapter(diffItemCallback)
        adapter.setList(listOf(1, 2, 3))
        adapter.getItemViewType(0)
    }

    @Test
    fun `adapter forwards events to the sections`() {
        val adapter = ListaAdapter(diffItemCallback)
        adapter.setSectionRegistry(ClassSectionRegistry().apply {
            register(FakeIntegerSection(fakeView, layoutId = 4))
        })
        adapter.setList(listOf(0, 1, 2))

        val viewHolder = adapter.onCreateViewHolder(fakeViewGroup, 4) as FakeViewHolder
        setViewType(viewHolder, 4)

        assertEquals(true, viewHolder.createdCalled)
        assertEquals(false, viewHolder.onBindCalled)

        adapter.onBindViewHolder(viewHolder, 0)
        assertEquals(true, viewHolder.onBindCalled)
        assertEquals(0, viewHolder.getItem())

        adapter.onViewDetachedFromWindow(viewHolder)
        assertEquals(true, viewHolder.onDetachedFromWindowCalled)

        adapter.onViewAttachedToWindow(viewHolder)
        assertEquals(true, viewHolder.onAttachedFromWindowCalled)

        adapter.onViewRecycled(viewHolder)
        assertEquals(true, viewHolder.onRecycledCalled)
        assertEquals(null, viewHolder.getItem())

        adapter.onBindViewHolder(viewHolder, 1, mutableListOf(2))
        assertEquals(true, viewHolder.onBindCalled)
        assertEquals(1, viewHolder.getItem())

        adapter.onFailedToRecycleView(viewHolder)
        assertEquals(true, viewHolder.onFailedToRecycleCalled)
        assertEquals(1, viewHolder.getItem())
    }

    // Since we're not using a real RecyclerView, we need to set the itemViewType ourselves
    private fun setViewType(viewHolder: RecyclerView.ViewHolder, itemViewType: Int) {
        val field = viewHolder::class.java.superclass!!.superclass!!
            .getDeclaredField("mItemViewType")
        field.isAccessible = true
        field.setInt(viewHolder, itemViewType)
    }
}