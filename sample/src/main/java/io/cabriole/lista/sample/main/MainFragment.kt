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

package io.cabriole.lista.sample.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.cabriole.lista.nested.ListaScrollStateManager
import io.cabriole.lista.sample.R
import io.cabriole.lista.sample.databinding.ScreenOptionsBinding

class MainFragment : Fragment(R.layout.screen_options) {

    private var _binding: ScreenOptionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var scrollStateManager: ListaScrollStateManager
    private lateinit var listController: MainListController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ScreenOptionsBinding.bind(view)

        scrollStateManager = ListaScrollStateManager(savedInstanceState)
        listController = MainListController(this, scrollStateManager)
        listController.setup(binding.recyclerView)

        loadItems()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        scrollStateManager.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadItems() {
        viewModel.loadItems()
        viewModel.getItems().observe(viewLifecycleOwner, Observer { list ->
            if (list != null) {
                listController.submitList(list)
            } else {
                listController.submitList(emptyList())
            }
        })
    }

}
