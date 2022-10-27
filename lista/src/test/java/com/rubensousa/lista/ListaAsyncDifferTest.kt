/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import androidx.recyclerview.widget.AsyncListDiffer.ListListener
import androidx.recyclerview.widget.DiffUtil
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.Collections.emptyList


class ListaAsyncDifferTest {

    companion object {
        private val STRING_DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                // items are the same if first char is the same
                return oldItem[0] == newItem[0]
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: String, newItem: String): Any? {
                if (newItem.startsWith(oldItem)) {
                    // new string is appended, return added portion on the end
                    return newItem.subSequence(oldItem.length, newItem.length)
                }
                return null
            }
        }
    }

    private val mainThreadExecutor = TestExecutor()
    private val backgroundThreadExecutor = TestExecutor()
    private lateinit var listener: Listener
    private lateinit var callback: Callback
    private lateinit var commitRunnable: CommitRunnable
    private lateinit var differ: ListaAsyncDiffer<String>

    @Before
    fun setup() {
        listener = Listener()
        callback = Callback()
        commitRunnable = CommitRunnable()
        differ = createDiffer(callback)
    }

    @After
    fun destroy(){
        differ.clearListListeners()
    }

    @Test
    fun initialState() {
        assertThat(differ.getCurrentList().size).isEqualTo(0)
        callback.assertNotCalled()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getEmpty() {
        differ.getCurrentList()[0]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getPastEnd() {
        differ.submitList(listOf("a", "b"))
        differ.getCurrentList()[2]
    }

    @Test
    fun getCurrentList() {
        // null is emptyList
        assertThat(differ.getCurrentList()).isEqualTo(emptyList<String>())

        val list = listOf("a", "b")
        differ.submitList(list)
        assertThat(differ.getCurrentList()).isEqualTo(list)
        assertThat(differ.getCurrentList()).isSameInstanceAs(list)

        // null again, empty again
        differ.submitList(null)
        assertThat(differ.getCurrentList()).isEqualTo(emptyList<String>())
    }

    @Test
    fun submitListSimple() {
        differ.submitList(listOf("a", "b"))

        assertThat(differ.getCurrentList().size).isEqualTo(2)
        assertThat(differ.getCurrentList()[0]).isEqualTo("a")
        assertThat(differ.getCurrentList()[1]).isEqualTo("b")

        callback.assertInsertion(position = 0, count = 2)
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertNoMoreInteractions()
    }

    @Test
    fun submitListReuse() {
        val differ = createDiffer(callback)
        val originalList = listOf("a", "b")

        // set up original list
        differ.submitList(originalList)
        callback.assertInsertion(position = 0, count = 2)
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertNoMoreInteractions()

        // submit new list, but don't let it finish
        differ.submitList(listOf("c", "d"))
        mainThreadExecutor.executeAll()
        callback.assertNoMoreInteractions()

        // resubmit original list, which should be final observable state
        differ.submitList(originalList)
        executePendingTasks()
        assertThat(differ.getCurrentList()).isEqualTo(originalList)
    }

    @Test
    fun nullsSkipCallback() {
        differ.submitList(listOf("a", "b"))
        executePendingTasks()
        callback.assertInsertion(position = 0, count = 2)

        differ.submitList(listOf("a", null))
        executePendingTasks()
        callback.assertRemoval(position = 1, count = 1)
        callback.assertInsertion(position = 1, count = 1)

        differ.submitList(listOf("b", null))
        executePendingTasks()
        callback.assertRemoval(position = 0, count = 1)
        callback.assertInsertion(position = 0, count = 1)

        callback.assertNoMoreInteractions()
    }

    @Test
    fun submitListUpdate() {
        // initial list (immediate)
        differ.submitList(listOf("a", "b"))
        callback.assertInsertion(position = 0, count = 2)
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertNoMoreInteractions()

        // update (deferred)
        differ.submitList(listOf("a", "b", "c"))
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertInsertion(position = 2, count = 1)
        callback.assertNoMoreInteractions()

        // clear (immediate)
        differ.submitList(null)
        callback.assertRemoval(position = 0, count = 3)
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertNoMoreInteractions()
    }

    @Test
    fun `submit only dispatches update after work is done in background thread`() {
        differ.submitList(listOf("1"))

        // First submit happens immediately
        assertThat(differ.getCurrentList()).isEqualTo(listOf("1"))

        differ.submitList(listOf("1", "2"))
        assertThat(differ.getCurrentList()).isEqualTo(listOf("1"))

        executePendingTasks()
        assertThat(differ.getCurrentList()).isEqualTo(listOf("1", "2"))
    }

    @Test
    fun `submit now dispatches update immediately`() {
        differ.addListListener(listener)
        differ.submitNow(listOf("1"))
        assertThat(differ.getCurrentList()).isEqualTo(listOf("1"))
        callback.assertInvalidated(expected = 1)

        executePendingTasks()
        callback.assertNoMoreInteractions()

        differ.submitNow(listOf("1", "2"))
        callback.assertInvalidated(expected = 1)

        executePendingTasks()
        callback.assertNoMoreInteractions()
    }

    @Test
    fun singleChangePayload() {
        differ.submitList(listOf("a", "b"))
        callback.assertInsertion(position = 0, count = 2)
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertNoMoreInteractions()

        differ.submitList(listOf("a", "beta"))
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertChange(position = 1, count = 1, payload = "eta")
        callback.assertNoMoreInteractions()
    }

    @Test
    fun multiChangePayload() {
        differ.submitList(listOf("a", "b"))
        callback.assertInsertion(position = 0, count = 2)
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertNoMoreInteractions()

        differ.submitList(listOf("alpha", "beta"))
        callback.assertNoMoreInteractions()
        executePendingTasks()
        callback.assertChange(position = 0, count = 1, payload = "lpha")
        callback.assertChange(position = 1, count = 1, payload = "eta")

        callback.assertNoMoreInteractions()
    }

    @Test
    fun listUpdatedBeforeListUpdateCallbacks() {
        // verify that itemCount is updated in the differ before dispatching ListUpdateCallbacks
        val expectedCount = intArrayOf(0)
        // provides access to differ, which must be constructed after callback
        val differAccessor = arrayOf<ListaAsyncDiffer<*>?>(null)

        val callback = object : ListaAsyncDiffer.UpdateCallback {

            override fun onInserted(position: Int, count: Int) {
                assertThat(differAccessor[0]!!.getCurrentList().size).isEqualTo(expectedCount[0])
            }

            override fun onRemoved(position: Int, count: Int) {
                assertThat(differAccessor[0]!!.getCurrentList().size).isEqualTo(expectedCount[0])
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                fail("not expected")
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                fail("not expected")
            }

            override fun onInvalidated() {
                fail("not expected")
            }
        }

        val differ = createDiffer(callback)
        differAccessor[0] = differ

        // in the fast-add case...
        expectedCount[0] = 3
        assertThat(differ.getCurrentList()).isEmpty()
        differ.submitList(listOf("a", "b", "c"))
        assertThat(differ.getCurrentList().size).isEqualTo(3)

        // in the slow, diff on BG thread case...
        expectedCount[0] = 6
        assertThat(differ.getCurrentList().size).isEqualTo(3)
        differ.submitList(listOf("a", "b", "c", "d", "e", "f"))
        executePendingTasks()
        assertThat(differ.getCurrentList().size).isEqualTo(6)

        // and in the fast-remove case
        expectedCount[0] = 0
        assertThat(differ.getCurrentList().size).isEqualTo(6)
        differ.submitList(null)
        assertThat(differ.getCurrentList().size).isEqualTo(0)
    }

    @Test
    fun listListener() {
        differ.addListListener(listener)


        // first - simple insert
        val first = listOf("a", "b")
        listener.assertNoMoreInteractions()
        differ.submitList(first, commitRunnable)
        listener.assertListChanged(emptyList(), first)
        listener.assertNoMoreInteractions()
        commitRunnable.assertExecuted()
        commitRunnable.assertNoMoreInteractions()

        // second - async update
        val second = listOf("c", "d")
        differ.submitList(second, commitRunnable)
        listener.assertNoMoreInteractions()
        commitRunnable.assertNoMoreInteractions()
        executePendingTasks()
        listener.assertListChanged(first, second)
        listener.assertNoMoreInteractions()
        commitRunnable.assertExecuted()
        commitRunnable.assertNoMoreInteractions()

        // third - same list - only triggers callback
        differ.submitList(second, commitRunnable)
        listener.assertNoMoreInteractions()
        commitRunnable.assertExecuted()
        commitRunnable.assertNoMoreInteractions()
        executePendingTasks()
        listener.assertNoMoreInteractions()
        commitRunnable.assertNoMoreInteractions()


        // fourth - null
        differ.submitList(null, commitRunnable)
        listener.assertListChanged(second, emptyList())
        listener.assertNoMoreInteractions()
        commitRunnable.assertExecuted()
        commitRunnable.assertNoMoreInteractions()

        // remove listener, see nothing
        differ.removeListListener(listener)
        differ.submitList(first)
        executePendingTasks()
        listener.assertNoMoreInteractions()

        // Add listener and clear, see nothing as well
        differ.addListListener(listener)
        differ.clearListListeners()
        differ.submitList(first)
        executePendingTasks()
        listener.assertNoMoreInteractions()
    }

    private fun executePendingTasks() {
        var executed: Boolean
        do {
            executed = backgroundThreadExecutor.executeAll()
            executed = mainThreadExecutor.executeAll() or executed
        } while (executed)
    }

    private fun createDiffer(
        listUpdateCallback: ListaAsyncDiffer.UpdateCallback
    ): ListaAsyncDiffer<String> {
        return ListaAsyncDiffer(
            listUpdateCallback,
            ListaAsyncDiffer.Config.buildTest(
                STRING_DIFF_CALLBACK,
                mainThreadExecutor,
                backgroundThreadExecutor
            )
        )
    }

    data class RangeEvent(val position: Int, val count: Int)
    data class MoveEvent(val fromPosition: Int, val toPosition: Int)
    data class UpdateEvent(val position: Int, val count: Int, val payload: Any?)
    data class ListUpdateEvent(val previousList: List<String>, val currentList: List<String>)

    class Listener : ListListener<String> {

        private val updateEvents = LinkedList<ListUpdateEvent>()

        override fun onCurrentListChanged(previousList: List<String>, currentList: List<String>) {
            updateEvents.add(ListUpdateEvent(previousList, currentList))
        }

        fun assertListChanged(previousList: List<String>, currentList: List<String>) {
            val event = updateEvents.removeFirst()
            assertThat(event.previousList).isEqualTo(previousList)
            assertThat(event.currentList).isEqualTo(currentList)
        }

        fun assertNoMoreInteractions() {
            if (updateEvents.isNotEmpty()) {
                fail("Found the following events: $updateEvents")
            }
        }
    }

    class CommitRunnable : Runnable {

        private var executions = 0

        override fun run() {
            executions++
        }

        fun assertNoMoreInteractions() {
            if (executions > 0) {
                fail("Found the following commit executions: $executions")
            }
            assertThat(executions).isEqualTo(0)
        }

        fun assertExecuted() {
            assertThat(executions).isEqualTo(1)
            executions--
        }

    }

    class Callback : ListaAsyncDiffer.UpdateCallback {

        private var insertEvents = LinkedList<RangeEvent>()
        private var removeEvents = LinkedList<RangeEvent>()
        private var moveEvents = LinkedList<MoveEvent>()
        private var updateEvents = LinkedList<UpdateEvent>()
        private var invalidateCalls = 0
        private var called = false

        override fun onInserted(position: Int, count: Int) {
            insertEvents.add(RangeEvent(position, count))
            called = true
        }

        override fun onRemoved(position: Int, count: Int) {
            removeEvents.add(RangeEvent(position, count))
            called = true
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            moveEvents.add(MoveEvent(fromPosition, toPosition))
            called = true
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            updateEvents.add(UpdateEvent(position, count, payload))
            called = true
        }

        override fun onInvalidated() {
            invalidateCalls++
            called = true
        }

        fun assertInsertion(position: Int, count: Int) {
            val insertion = insertEvents.removeFirst()
            assertThat(insertion.position).isEqualTo(position)
            assertThat(insertion.count).isEqualTo(count)
        }

        fun assertRemoval(position: Int, count: Int) {
            val removal = removeEvents.removeFirst()
            assertThat(removal.position).isEqualTo(position)
            assertThat(removal.count).isEqualTo(count)
        }

        fun assertChange(position: Int, count: Int, payload: Any?) {
            val update = updateEvents.removeFirst()
            assertThat(update.position).isEqualTo(position)
            assertThat(update.count).isEqualTo(count)
            assertThat(update.payload).isEqualTo(payload)
        }

        fun assertInvalidated(expected: Int) {
            assertThat(invalidateCalls).isEqualTo(expected)
            invalidateCalls = 0
        }

        fun assertMove(fromPosition: Int, toPosition: Int) {
            val move = moveEvents.removeFirst()
            assertThat(move.fromPosition).isEqualTo(fromPosition)
            assertThat(move.toPosition).isEqualTo(toPosition)
        }

        fun assertNoMoreInteractions() {
            if (insertEvents.isNotEmpty()
                || removeEvents.isNotEmpty()
                || moveEvents.isNotEmpty()
                || updateEvents.isNotEmpty()
                || invalidateCalls > 0
            ) {
                fail(
                    "There were callback interactions: insertions -> $insertEvents, " +
                            "removals -> $removeEvents, moves -> $moveEvents, " +
                            "updates -> $updateEvents, invalidations -> $invalidateCalls"
                )
            }
        }

        fun assertNotCalled() {
            assertThat(called).isFalse()
        }
    }


}