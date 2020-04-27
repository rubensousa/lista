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

package io.cabriole.lista.testing.assertions

import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

/**
 * A collection of useful Assertions for [RecyclerView]
 */
object ListaAssertions {

    @JvmStatic
    fun withItemCount(expectedCount: Int): ItemCountAssertion {
        return withItemCount(CoreMatchers.`is`(expectedCount))
    }

    @JvmStatic
    fun withItemCount(matcher: Matcher<Int>): ItemCountAssertion {
        return ItemCountAssertion(matcher)
    }

}
