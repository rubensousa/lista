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

package com.rubensousa.lista.testing.matchers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf

object ListaNestedMatchers {

    @JvmStatic
    fun withAscendant(ascendantMatcher: Matcher<View>): WithAscendantMatcher {
        return WithAscendantMatcher(ascendantMatcher)
    }

    /**
     * If the RecyclerView is at the itemView level, use [withNestedRecyclerView] instead
     *
     * @param sectionMatcher a matcher that identifies the nested section that contains a RecyclerView
     *
     * @param childRecyclerViewMatcher a matcher that identifies the nested RecyclerView
     *
     *
     * @return a matcher that identifies a nested RecyclerView
     *
     */
    @JvmStatic
    fun withNestedRecyclerViewInSection(
        sectionMatcher: Matcher<View>,
        childRecyclerViewMatcher: Matcher<View>
    ): Matcher<View> {
        return allOf(
            childRecyclerViewMatcher,
            withAscendant(sectionMatcher),
            withAscendant(isAssignableFrom(RecyclerView::class.java))
        )
    }

    /**
     * If the RecyclerView is at the itemView level, use [withNestedRecyclerView] instead
     *
     * @param sectionMatcher a matcher that identifies the nested section that contains a RecyclerView
     *
     * @param childRecyclerViewMatcher a matcher that identifies the nested RecyclerView
     *
     * @param parentRecyclerViewMatcher a matcher that identifies the RecyclerView parent
     * of the nested RecyclerView
     *
     * @return a matcher that identifies a nested RecyclerView
     *
     */
    @JvmStatic
    fun withNestedRecyclerViewInSection(
        sectionMatcher: Matcher<View>,
        childRecyclerViewMatcher: Matcher<View>,
        parentRecyclerViewMatcher: Matcher<View>
    ): Matcher<View> {
        return allOf(
            childRecyclerViewMatcher,
            withAscendant(sectionMatcher),
            withAscendant(parentRecyclerViewMatcher)
        )
    }

    /**
     * If the RecyclerView is at the itemView level, use [withNestedRecyclerView] instead
     *
     * @param sectionMatcher a matcher that identifies the nested section that contains a RecyclerView
     *
     * @param childRecyclerViewId the id of the nested RecyclerView
     *
     * @param parentRecyclerViewId the id of the RecyclerView parent of the nested RecyclerView
     *
     * @return a matcher that identifies a nested RecyclerView tha
     */
    @JvmStatic
    fun withNestedRecyclerViewInSection(
        sectionMatcher: Matcher<View>,
        childRecyclerViewId: Int,
        parentRecyclerViewId: Int
    ): Matcher<View> {
        return withNestedRecyclerViewInSection(
            sectionMatcher,
            withId(childRecyclerViewId),
            withId(parentRecyclerViewId)
        )
    }

    /**
     * If the RecyclerView is inside the itemView of the ViewHolder,
     * use [withNestedRecyclerViewInSection] instead
     *
     * @param childRecyclerViewMatcher a matcher that identifies the nested RecyclerView
     *
     * @param parentRecyclerViewMatcher a matcher that identifies the RecyclerView parent
     * of the nested RecyclerView
     *
     * @return a matcher that identifies a nested RecyclerView
     */
    @JvmStatic
    fun withNestedRecyclerView(
        childRecyclerViewMatcher: Matcher<View>,
        parentRecyclerViewMatcher: Matcher<View>
    ): Matcher<View> {
        return allOf(
            childRecyclerViewMatcher,
            withAscendant(parentRecyclerViewMatcher)
        )
    }

    /**
     * @param childRecyclerViewMatcher a matcher that identifies the nested RecyclerView
     *
     * @param parentRecyclerViewId the id of the parent RecyclerView
     *
     * @return a matcher that identifies a nested RecyclerView
     */
    @JvmStatic
    fun withNestedRecyclerView(
        childRecyclerViewMatcher: Matcher<View>,
        parentRecyclerViewId: Int
    ): Matcher<View> {
        return withNestedRecyclerView(
            childRecyclerViewMatcher,
            withId(parentRecyclerViewId)
        )
    }

    /**
     * @param recyclerViewMatcher a matcher that identifies the RecyclerView
     *
     * @param sectionMatcher a matcher that identifies the nested section
     *
     * @param itemMatcher a matcher that identifies an itemView
     * inside a section that matches [sectionMatcher]
     *
     * @return a matcher that identifies an itemView inside a RecyclerView
     */
    @JvmStatic
    fun withNestedView(
        sectionMatcher: Matcher<View>,
        recyclerViewMatcher: Matcher<View>,
        itemMatcher: Matcher<View>
    ): Matcher<View> {
        return allOf(
            itemMatcher,
            withAscendant(recyclerViewMatcher),
            withAscendant(sectionMatcher)
        )
    }

    /**
     * @param sectionMatcher a matcher that identifies the nested section
     *
     * @param itemMatcher a matcher that identifies an itemView
     * inside a section that matches [sectionMatcher]
     *
     * @return a matcher that identifies an itemView inside a RecyclerView
     */
    @JvmStatic
    fun withNestedView(
        sectionMatcher: Matcher<View>,
        itemMatcher: Matcher<View>
    ): Matcher<View> {
        return allOf(
            itemMatcher,
            withAscendant(isAssignableFrom(RecyclerView::class.java)),
            withAscendant(sectionMatcher)
        )
    }

    /**
     * @param recyclerViewMatcher a matcher that identifies the RecyclerView
     *
     * @param sectionMatcher a matcher that identifies the nested section
     *
     * @param itemMatcher a matcher that identifies an itemView
     * inside a section that matches [sectionMatcher]
     *
     * @param childMatcher a matcher that identifies a child view
     * of an itemView that matches [itemMatcher]
     *
     * @return a matcher that identifies a view inside a RecyclerView's itemView
     */
    @JvmStatic
    fun withNestedChildView(
        sectionMatcher: Matcher<View>,
        recyclerViewMatcher: Matcher<View>,
        itemMatcher: Matcher<View>,
        childMatcher: Matcher<View>
    ): Matcher<View> {
        return allOf(
            childMatcher,
            withAscendant(itemMatcher),
            withAscendant(recyclerViewMatcher),
            withAscendant(sectionMatcher)
        )
    }

    /**
     * @param sectionMatcher a matcher that identifies the nested section
     *
     * @param itemMatcher a matcher that identifies an itemView
     * inside a section that matches [sectionMatcher]
     *
     * @param childMatcher a matcher that identifies a child view
     * of an itemView that matches [itemMatcher]
     *
     * @return a matcher that identifies a view inside a RecyclerView's itemView
     */
    @JvmStatic
    fun withNestedChildView(
        sectionMatcher: Matcher<View>,
        itemMatcher: Matcher<View>,
        childMatcher: Matcher<View>
    ): Matcher<View> {
        return withNestedChildView(
            sectionMatcher,
            isAssignableFrom(RecyclerView::class.java),
            itemMatcher,
            childMatcher
        )
    }

    /**
     * Checks if a View has an ascendant that that matches [ascendantMatcher]
     */
    class WithAscendantMatcher(private val ascendantMatcher: Matcher<View>) :
        TypeSafeMatcher<View>() {

        override fun describeTo(description: Description) {
            description.appendText("has ascendant matching: ")
            ascendantMatcher.describeTo(description)
        }

        public override fun matchesSafely(view: View): Boolean {
            var parent = view.parent
            while (parent != null) {
                if (ascendantMatcher.matches(parent)) {
                    return true
                }
                parent = parent.parent
            }
            return false
        }
    }

}
