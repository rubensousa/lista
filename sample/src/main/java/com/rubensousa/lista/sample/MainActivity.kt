/*
 * Copyright 2023 Rúben Sousa
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

package com.rubensousa.lista.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rubensousa.lista.pool.clearSharedRecycledViewPool
import com.rubensousa.lista.pool.installSharedRecycledViewPool

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSharedRecycledViewPool()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = RecyclerViewFragment()
            transaction.add(R.id.container, fragment, RecyclerViewFragment.TAG)
                .setPrimaryNavigationFragment(fragment)
                .commitNow()
        }
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_recyclerview -> showFragment(RecyclerViewFragment.TAG)
                R.id.navigation_compose -> showFragment(ComposeFragment.TAG)
            }
            true
        }
    }

    private fun showFragment(tag: String) {
        val currentFragment = supportFragmentManager.primaryNavigationFragment
        var targetFragment = supportFragmentManager.findFragmentByTag(tag)
        if (targetFragment == null) {
            targetFragment = when (tag) {
                RecyclerViewFragment.TAG -> RecyclerViewFragment()
                else -> ComposeFragment()
            }
        }
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val transaction = supportFragmentManager.beginTransaction()
            if (currentFragment != null) {
                transaction.hide(currentFragment)
            }
            transaction.setPrimaryNavigationFragment(targetFragment)
            transaction.add(R.id.container, targetFragment, tag)
            transaction.commitNowAllowingStateLoss()
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        transaction.show(targetFragment)
        transaction.setPrimaryNavigationFragment(targetFragment)
        transaction.commitNowAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearSharedRecycledViewPool()
    }

}
