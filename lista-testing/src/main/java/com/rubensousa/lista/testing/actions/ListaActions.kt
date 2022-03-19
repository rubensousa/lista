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

package com.rubensousa.lista.testing.actions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import org.hamcrest.Matcher

/**
 * A collection of useful Actions for [RecyclerView]
 */
object ListaActions {

    @JvmStatic
    fun waitForItemViewLayout(): ViewAction {
        return com.rubensousa.lista.testing.actions.WaitForItemViewLayoutAction()
    }

    @JvmStatic
    fun smoothScrollTo(position: Int): ViewAction {
        return com.rubensousa.lista.testing.actions.SmoothScrollToPositionAction(position)
    }

    /**
     * Scroll to a section that matches [itemViewMatcher]
     *
     * @param itemViewMatcher a matcher that identifies the nested section
     * @param itemMatchIndex if there are multiple matches, this will be the index used
     */
    @JvmStatic
    fun scrollToNestedView(
        itemViewMatcher: Matcher<View>,
        itemMatchIndex: Int = 0
    ): ViewAction {
        return RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(itemViewMatcher)
            .atPosition(itemMatchIndex)
    }

    /**
     * Performs a [ViewAction] on a nested item inside a section
     *
     * @param itemViewMatcher a matcher that identifies the item
     * @param itemMatchIndex if there are multiple matches, this will be the index used
     * @param itemAction the [ViewAction] to perform on the item
     */
    @JvmStatic
    fun performOnNestedView(
        itemViewMatcher: Matcher<View>,
        itemAction: ViewAction,
        itemMatchIndex: Int = 0
    ): ViewAction {
        return RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
            itemViewMatcher,
            itemAction
        ).atPosition(itemMatchIndex)
    }

    /**
     * Clicks on a nested item of a section
     *
     * @param itemViewMatcher a matcher that identifies the item
     * @param itemMatchIndex if there are multiple matches, this will be the index used
     */
    @JvmStatic
    fun clickOnNestedView(
        itemViewMatcher: Matcher<View>,
        itemMatchIndex: Int = 0
    ): ViewAction {
        return com.rubensousa.lista.testing.actions.ListaActions.performOnNestedView(
            itemViewMatcher,
            ViewActions.click(),
            itemMatchIndex
        )
    }

    /**
     * Performs a long click on a nested item of a section
     *
     * @param itemViewMatcher a matcher that identifies the item
     * @param itemMatchIndex if there are multiple matches, this will be the index used
     */
    @JvmStatic
    fun longClickOnNestedView(
        itemViewMatcher: Matcher<View>,
        itemMatchIndex: Int = 0
    ): ViewAction {
        return com.rubensousa.lista.testing.actions.ListaActions.performOnNestedView(
            itemViewMatcher,
            ViewActions.click(),
            itemMatchIndex
        )
    }


}
