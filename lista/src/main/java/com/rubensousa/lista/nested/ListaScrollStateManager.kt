/*
 * Copyright 2019 Rúben Sousa
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

package com.rubensousa.lista.nested

import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.lista.ListaViewHolder
import com.rubensousa.lista.R

/**
 * Persists scroll state for nested RecyclerViews.
 *
 * 1. Call [saveScrollState] in [ListaViewHolder.onRecycled] to save the scroll position.
 * If you're recycling detached views, you'll also need to call it in
 * [ListaViewHolder.onDetachedFromWindow].
 *
 * 2. Call [restoreScrollState] in [ListaViewHolder.onBound]
 * after changing the adapter contents to restore the scroll position.
 * If you're recycling detached views, you'll also need to call it in
 * [ListaViewHolder.onAttachedToWindow]
 */
class ListaScrollStateManager {

    companion object {
        const val STATE_BUNDLE = "scroll_state_bundle"
        const val ARG_KEY = "arg_scroll_state"
    }

    /**
     * Persists the [RecyclerView.LayoutManager] state
     */
    private val scrollState = hashMapOf<String, Parcelable?>()

    /**
     * Keeps track of the keys that point to RecyclerViews
     * that have new scroll states that should be saved
     */
    private val scrolledKeys = mutableSetOf<String>()

    fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    saveScrollState(recyclerView)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val key = getScrollStateKey(recyclerView)
                if (key != null) {
                    if (dx != 0 || dy != 0) {
                        scrolledKeys.add(key)
                    }
                }
            }
        })
    }


    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.getBundle(STATE_BUNDLE)?.let { bundle ->
            bundle.keySet().forEach { key ->
                scrollState[key] = bundle.getParcelable(key)
            }
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        val stateBundle = Bundle()
        scrollState.entries.forEach { entry ->
            stateBundle.putParcelable(entry.key, entry.value)
        }
        outState.putBundle(STATE_BUNDLE, stateBundle)
    }

    fun clear() {
        scrollState.clear()
        scrolledKeys.clear()
    }

    /**
     * Saves this RecyclerView layout state for a given key
     */
    fun saveScrollState(recyclerView: RecyclerView) {
        val key = getScrollStateKey(recyclerView) ?: return
        // Check if we scrolled the RecyclerView for this key
        if (scrolledKeys.contains(key)) {
            val layoutManager = recyclerView.layoutManager ?: return
            scrollState[key] = layoutManager.onSaveInstanceState()
            scrolledKeys.remove(key)
        }
        setScrollStateKey(recyclerView, null)
    }

    /**
     * Restores this RecyclerView layout state for a given key
     */
    fun restoreScrollState(recyclerView: RecyclerView, key: String) {
        val layoutManager = recyclerView.layoutManager ?: return
        val savedState = scrollState[key]
        if (savedState != null) {
            layoutManager.onRestoreInstanceState(savedState)
        } else {
            // If we don't have any state for this RecyclerView,
            // make sure we reset the scroll position
            layoutManager.scrollToPosition(0)
        }
        // Mark this key as not scrolled since we just restored the state
        scrolledKeys.remove(key)
        setScrollStateKey(recyclerView, key)
    }

    private fun setScrollStateKey(recyclerView: RecyclerView, key: String?) {
        recyclerView.setTag(R.id.scroll_state_tag_key, key)
    }

    private fun getScrollStateKey(recyclerView: RecyclerView): String? {
        return recyclerView.getTag(R.id.scroll_state_tag_key) as String?
    }

}
