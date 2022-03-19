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

package com.rubensousa.lista.sample

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.rubensousa.lista.sample.main.MainActivity
import com.rubensousa.lista.testing.actions.ListaActions
import com.rubensousa.lista.testing.actions.ListaActions.smoothScrollTo
import com.rubensousa.lista.testing.matchers.ListaNestedMatchers.withNestedRecyclerView
import com.rubensousa.lista.testing.matchers.ListaNestedMatchers.withNestedView
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Simple tests that perform scrolling on RecyclerViews
 *
 */
@RunWith(AndroidJUnit4::class)
class ScrollingTests {

    companion object {
        const val PARENT_RECYCLERVIEW = R.id.recyclerView
        const val CHILD_RECYCLERVIEW = R.id.cardRecyclerView
        const val NESTED_POSITION = 5
        const val NESTED_VIEW_POSITION = 19
    }

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, true)

    @Test
    fun testSmoothScrollingMainList() {
        onView(withId(PARENT_RECYCLERVIEW))
            .perform(com.rubensousa.lista.testing.actions.ListaActions.waitForItemViewLayout())
            .perform(smoothScrollTo(8))
            .perform(smoothScrollTo(0))
    }


    @Test
    fun testSmoothScrollingNestedList() {
        onView(withId(PARENT_RECYCLERVIEW)).perform(
            scrollTo<RecyclerView.ViewHolder>(withTagValue(`is`(NESTED_POSITION)))
        )
        onView(
            withNestedRecyclerView(
                childRecyclerViewMatcher = withTagValue(`is`(NESTED_POSITION)),
                parentRecyclerViewId = PARENT_RECYCLERVIEW
            )
        ).perform(smoothScrollTo(NESTED_VIEW_POSITION))
    }

    @Test
    fun testScrollingNestedList() {
        // Scroll the parent RecyclerView to the nested section
        onView(withId(PARENT_RECYCLERVIEW))
            .perform(scrollTo<RecyclerView.ViewHolder>(withTagValue(`is`(NESTED_POSITION))))

        // Scroll the nested section to the item position we're interested in
        onView(
            withNestedRecyclerView(
                childRecyclerViewMatcher = withTagValue(`is`(NESTED_POSITION)),
                parentRecyclerViewId = PARENT_RECYCLERVIEW
            )
        ).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(NESTED_VIEW_POSITION))

        // Check if the card is shown
        onView(
            withNestedView(
                itemMatcher = withTagValue(`is`(NESTED_VIEW_POSITION)),
                sectionMatcher = withTagValue(`is`(NESTED_POSITION))
            )
        ).check(ViewAssertions.matches(isDisplayed()))
    }

}
