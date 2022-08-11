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

package com.rubensousa.lista.sample.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.lista.pool.ActivityRecycledViewPoolProvider
import com.rubensousa.lista.pool.ListaUnboundedViewPool
import com.rubensousa.lista.sample.R

class MainActivity : AppCompatActivity(R.layout.activity_main), ActivityRecycledViewPoolProvider {

    private val viewPool = ListaUnboundedViewPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment())
                .commitNow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPool.clear()
    }

    override fun getActivityRecycledViewPool(): RecyclerView.RecycledViewPool {
        return viewPool
    }

}
