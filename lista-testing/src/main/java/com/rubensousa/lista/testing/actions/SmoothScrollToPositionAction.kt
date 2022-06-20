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
import androidx.test.espresso.*
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher


/**
 * An Action that smooth scrolls a RecyclerView to [position]
 */
class SmoothScrollToPositionAction(private val position: Int) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())
    }

    override fun getDescription(): String {
        return "smooth scroll RecyclerView to position: $position"
    }

    override fun perform(uiController: UiController, view: View) {
        val recyclerView = view as RecyclerView
        val idlingResource = ScrollingIdlingResource(recyclerView)
        IdlingRegistry.getInstance().register(idlingResource)
        recyclerView.smoothScrollToPosition(position)
        uiController.loopMainThreadForAtLeast(300L)
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    class ScrollingIdlingResource(
        private val recyclerView: RecyclerView,
        private var callback: IdlingResource.ResourceCallback? = null
    ) : IdlingResource {

        override fun getName(): String = this::class.simpleName ?: ""

        override fun isIdleNow(): Boolean {
            val layoutManager = recyclerView.layoutManager ?: return true
            val isSmoothScrolling = layoutManager.isSmoothScrolling
                    && recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE
            if (!isSmoothScrolling) {
                callback?.onTransitionToIdle()
            }
            return !isSmoothScrolling
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }

    }

}
