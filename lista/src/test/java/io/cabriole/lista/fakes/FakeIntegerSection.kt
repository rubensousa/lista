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

package io.cabriole.lista.fakes

import android.view.View
import android.view.ViewGroup
import io.cabriole.lista.ListaSection
import io.cabriole.lista.ListaSectionViewHolder

class FakeIntegerSection(
    private var fakeView: View,
    layoutId: Int
) : ListaSection<Int>(layoutId = layoutId) {

    override fun inflateLayout(parent: ViewGroup, layoutId: Int): View {
        return fakeView
    }

    override fun onCreateViewHolder(view: View): ListaSectionViewHolder<Int> {
        return FakeViewHolder(view)
    }

    override fun isForItem(item: Any): Boolean = item is Int

}