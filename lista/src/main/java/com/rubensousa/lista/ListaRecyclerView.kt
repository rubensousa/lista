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

package com.rubensousa.lista

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * A RecyclerView that only handles scroll events with the same orientation of its LayoutManager.
 * Avoids situations where nested RecyclerViews don't receive touch events properly.
 */
class ListaRecyclerView : RecyclerView {

    private var lastX = 0.0f
    private var lastY = 0.0f
    private var scrolling = false
    private val scrollListener = object : OnScrollListener() {
        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int
        ) {
            super.onScrollStateChanged(recyclerView, newState)
            scrolling = newState != SCROLL_STATE_IDLE
        }
    }

    constructor(context: Context) : this(context, null)

    @SuppressLint("PrivateResource")
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        androidx.recyclerview.R.attr.recyclerViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        addOnScrollListener(scrollListener)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val lm = layoutManager ?: return super.onInterceptTouchEvent(e)
        var allowScroll = true
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.x
                lastY = e.y
                // If we were scrolling, stop now by faking a touch release
                if (scrolling) {
                    val newEvent = MotionEvent.obtain(e)
                    newEvent.action = MotionEvent.ACTION_UP
                    return super.onInterceptTouchEvent(newEvent)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // We're moving, so check if we're trying
                // to scroll vertically or horizontally so we don't intercept the wrong event.
                val currentX = e.x
                val currentY = e.y
                val dx = Math.abs(currentX - lastX)
                val dy = Math.abs(currentY - lastY)
                allowScroll = if (dy > dx) lm.canScrollVertically() else lm.canScrollHorizontally()
            }
        }

        if (!allowScroll) {
            return false
        }

        return super.onInterceptTouchEvent(e)
    }

}
