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

package com.rubensousa.lista.testing.extensions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.rubensousa.lista.testing.actions.ListaActions.smoothScrollTo
import com.rubensousa.lista.testing.matchers.ListaNestedMatchers.withAscendant
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf

fun Int.recyclerViewPerformOnPosition(position: Int, viewAction: ViewAction) {
    onView(withId(this))
        .perform(
            RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(position, viewAction)
        )
}

fun Int.recyclerViewLongClickOnPosition(position: Int) {
    recyclerViewPerformOnPosition(position, ViewActions.longClick())
}

fun Int.recyclerViewClickOnPosition(position: Int) {
    recyclerViewPerformOnPosition(position, ViewActions.click())
}

fun Int.recyclerViewPerformOnView(matcher: Matcher<View>, action: ViewAction, matchIndex: Int = 0) {
    onView(withId(this))
        .perform(
            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                matcher, action
            ).atPosition(matchIndex)
        )
}

fun Int.recyclerViewClickOnView(matcher: Matcher<View>, matchIndex: Int = 0) {
    recyclerViewPerformOnView(matcher, ViewActions.click(), matchIndex)
}

fun Int.recyclerViewLongClickOnView(matcher: Matcher<View>, matchIndex: Int = 0) {
    recyclerViewPerformOnView(matcher, ViewActions.longClick(), matchIndex)
}

fun Int.recyclerViewPerformOnChildView(
    itemViewMatcher: Matcher<View>,
    childViewMatcher: Matcher<View>,
    viewAction: ViewAction
) {
    onView(
        allOf(
            withAscendant(allOf(isAssignableFrom(RecyclerView::class.java), withId(this))),
            withAscendant(itemViewMatcher),
            childViewMatcher
        )
    ).perform(viewAction)
}

fun Int.recyclerViewClickOnChildView(
    itemViewMatcher: Matcher<View>,
    childViewMatcher: Matcher<View>
) {
    recyclerViewPerformOnChildView(itemViewMatcher, childViewMatcher, ViewActions.click())
}

fun Int.recyclerViewLongClickOnChildView(
    itemViewMatcher: Matcher<View>,
    childViewMatcher: Matcher<View>
) {
    recyclerViewPerformOnChildView(itemViewMatcher, childViewMatcher, ViewActions.longClick())
}

fun Int.recyclerViewScrollTo(position: Int) {
    onView(withId(this))
        .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
}

fun Int.recyclerViewScrollTo(itemViewMatcher: Matcher<View>, matchIndex: Int = 0) {
    onView(withId(this))
        .perform(
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(itemViewMatcher)
                .atPosition(matchIndex)
        )
}

fun Int.recyclerViewSmoothScrollTo(position: Int) {
    onView(withId(this)).perform(smoothScrollTo(position))
}
