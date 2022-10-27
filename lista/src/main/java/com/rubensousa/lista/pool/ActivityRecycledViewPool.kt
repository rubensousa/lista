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

package com.rubensousa.lista.pool

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.lista.R

/**
 * Can be used to provide a shared [RecyclerView.RecycledViewPool] across multiple fragments
 * for maximum re-use of Views.
 *
 * You can get this instance in Fragments via [Fragment.getActivityScopedRecycledViewPool],
 * in Views via [View.getActivityScopedRecycledViewPool]
 * or in ViewHolders via [RecyclerView.ViewHolder.getActivityScopedRecycledViewPool].
 *
 */
fun Activity.installSharedRecycledViewPool(
    viewPool: RecyclerView.RecycledViewPool = ListaUnboundedViewPool()
) {
    window.decorView.setTag(R.id.activity_scoped_recycledviewpool, viewPool)
}

fun Activity.clearSharedRecycledViewPool() {
    val viewPool = window.decorView.getTag(R.id.activity_scoped_recycledviewpool)
    if (viewPool !is RecyclerView.RecycledViewPool) {
        return
    }
    viewPool.clear()
}

internal fun Activity.findSharedRecycledViewPool(): RecyclerView.RecycledViewPool? {
    val viewPool = window.decorView.getTag(R.id.activity_scoped_recycledviewpool)
    if (viewPool !is RecyclerView.RecycledViewPool) {
        return null
    }
    return viewPool
}

fun Fragment.getActivityScopedRecycledViewPool(): RecyclerView.RecycledViewPool {
    return requireActivity().findSharedRecycledViewPool()
        ?: throw IllegalStateException("Your Fragment's parent activity didn't call installSharedRecycledViewPool")
}

fun View.getActivityScopedRecycledViewPool(): RecyclerView.RecycledViewPool {
    return findViewActivity(this).findSharedRecycledViewPool()
        ?: throw IllegalStateException("Your View's parent activity didn't call installSharedRecycledViewPool")
}

fun RecyclerView.ViewHolder.getActivityScopedRecycledViewPool(): RecyclerView.RecycledViewPool {
    return itemView.getActivityScopedRecycledViewPool()
}

private fun findViewActivity(view: View): Activity {
    var context = view.context
    while (context is ContextWrapper && context !is Activity) {
        context = context.baseContext
    }
    return context as Activity
}
