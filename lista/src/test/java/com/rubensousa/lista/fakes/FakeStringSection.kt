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

package com.rubensousa.lista.fakes

import android.view.View
import android.view.ViewGroup
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaSectionViewHolder

class FakeStringSection(
    private var fakeView: View,
    layoutId: Int
) : ListaSection<String, FakeViewHolder<String>>(layoutId = layoutId) {

    override fun inflateLayout(parent: ViewGroup, layoutId: Int): View {
        return fakeView
    }

    override fun onCreateViewHolder(view: View): FakeViewHolder<String> {
        return FakeViewHolder(view)
    }

}
