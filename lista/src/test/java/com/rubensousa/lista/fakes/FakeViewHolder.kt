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
import com.rubensousa.lista.ListaSectionViewHolder

class FakeViewHolder<T : Any>(view: View) : ListaSectionViewHolder<T>(view) {

    var createdCalled = false
    var onBindCalled = false
    var onDetachedFromWindowCalled = false
    var onAttachedFromWindowCalled = false
    var onRecycledCalled = false
    var onFailedToRecycleCalled = false

    override fun onCreated() {
        super.onCreated()
        createdCalled = true
    }

    override fun onBind(item: T, payloads: List<Any>) {
        super.onBind(item, payloads)
        onBindCalled = true
        onRecycledCalled = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDetachedFromWindowCalled = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onDetachedFromWindowCalled = false
        onAttachedFromWindowCalled = true
    }

    override fun onRecycled() {
        super.onRecycled()
        onRecycledCalled = true
        onFailedToRecycleCalled = false
    }

    override fun onFailedToRecycle(): Boolean {
        onRecycledCalled = false
        onFailedToRecycleCalled = true
        return super.onFailedToRecycle()
    }

}
