/*
 * Copyright (c) 2020. Cabriole
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

package io.cabriole.lista

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.cabriole.lista.fakes.FakeIntegerSection
import io.cabriole.lista.fakes.FakeStringSection
import io.cabriole.lista.fakes.FakeViewHolder
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.lang.IllegalStateException

@RunWith(MockitoJUnitRunner::class)
class AdapterTests {

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

    @Test
    fun testRegisteringSections() {
        val adapter = ListaAdapter(diffItemCallback)
        adapter.setList(listOf(1, "Test"))

        val integerSection = FakeIntegerSection(fakeView, layoutId = 4)
        adapter.addSection(integerSection)

        val stringSection = FakeStringSection(fakeView, layoutId = 5)
        adapter.addSection(stringSection)

        assertEquals(integerSection, adapter.getSection(integerSection.getItemViewType()))
        assertEquals(stringSection, adapter.getSection(stringSection.getItemViewType()))

        assertEquals(integerSection.getItemViewType(), adapter.getItemViewType(0))
        assertEquals(stringSection.getItemViewType(), adapter.getItemViewType(1))


        val sections: List<ListaSection<Any>> = adapter.getSections()
        assertEquals(true, sections.contains(integerSection as ListaSection<*>))
        assertEquals(true, sections.contains(stringSection as ListaSection<*>))

        adapter.removeSection(stringSection)
        assertEquals(null, adapter.getSection(stringSection.getItemViewType()))
    }

    @Test(expected = IllegalStateException::class)
    fun testMissingRegisteredSection() {
        val adapter = ListaAdapter(diffItemCallback)
        adapter.setList(listOf(1, 2, 3))
        adapter.getItemViewType(0)
    }

    @Test
    fun testAdapterEvents() {
        val adapter = ListaAdapter(diffItemCallback)
        adapter.setList(listOf(0, 1, 2))

        val fakeSection = FakeIntegerSection(fakeView, layoutId = 4)
        adapter.addSection(fakeSection)

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
        assertEquals(true, viewHolder.onBindWithPayloadCalled)
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