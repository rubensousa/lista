/*
 * Copyright 2022 Rúben Sousa
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
import com.rubensousa.lista.ListaViewHolder
import io.mockk.mockk

class TestViewHolder<T>(view: View) : ListaViewHolder<T>(view) {

    companion object {

        fun <T> create(): TestViewHolder<T> {
            val mockedView : View = mockk()
            return TestViewHolder(mockedView)
        }
    }

    var payloads: List<Any>? = null
    var createdCalls = 0
    var recycledCalls = 0
    var bindCalls = 0
    var attachedToWindowCalls = 0
    var detachedFromWindowCalls = 0
    var failedToRecycleCalls = 0

    override fun onCreated() {
        super.onCreated()
        createdCalls++
    }

    override fun onRecycled() {
        super.onRecycled()
        recycledCalls++
    }

    override fun onFailedToRecycle(): Boolean {
        failedToRecycleCalls++
        return super.onFailedToRecycle()
    }

    override fun onBound(item: T, payloads: List<Any>) {
        super.onBound(item, payloads)
        this.payloads = payloads
        bindCalls++
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindowCalls++
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        detachedFromWindowCalls++
    }

}
