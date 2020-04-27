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

package io.cabriole.lista.testing.actions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

/**
 * An Action that waits until a RecyclerView has at least one item laid out
 */
class WaitForItemViewLayoutAction : ViewAction {

    override fun getDescription(): String {
        return "Waiting for RecyclerView layout"
    }

    override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(RecyclerView::class.java)
    }

    override fun perform(uiController: UiController?, view: View) {
        if (view !is RecyclerView) {
            return
        }
        var itemView = getView(view)
        while (itemView == null || (!itemView.isLaidOut || itemView.height == 0)) {
            itemView = getView(view)
        }
    }

    private fun getView(view: RecyclerView): View? {
        if (view.adapter == null) {
            return null
        }
        return view.getChildAt(0)
    }

}
