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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.rubensousa.decorator.DecorationLookup
import com.rubensousa.decorator.GridSpanMarginDecoration
import com.rubensousa.decorator.LinearBoundsMarginDecoration
import com.rubensousa.decorator.LinearMarginDecoration
import com.rubensousa.lista.item.ListaItemAdapter
import com.rubensousa.lista.item.ListaItemSpanSizeLookup
import com.rubensousa.lista.nested.ListaScrollStateManager
import com.rubensousa.lista.sample.databinding.ScreenOptionsBinding
import com.rubensousa.lista.sample.recyclerview.BigCardRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.SmallCardRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.CardListRecyclerViewItem
import com.rubensousa.lista.sample.recyclerview.OptionRecyclerViewItem
import com.rubensousa.lista.section.ListaMutableArgs

class RecyclerViewFragment : Fragment(R.layout.screen_options) {

    companion object {
        const val TAG = "RecyclerViewFagment"
    }

    private var _binding: ScreenOptionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecyclerViewViewModel by viewModels()
    private lateinit var scrollStateManager: ListaScrollStateManager
    private lateinit var adapter: ListaItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ScreenOptionsBinding.bind(view)
        scrollStateManager = ListaScrollStateManager()
        scrollStateManager.onRestoreInstanceState(savedInstanceState)
        val args = ListaMutableArgs()
        args.set(ListaScrollStateManager.ARG_KEY, scrollStateManager)
        adapter = ListaItemAdapter(args)
        setupRecyclerView(binding.recyclerView)

        viewModel.getListItems().observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        scrollStateManager.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(recyclerView.context, 2)
        layoutManager.spanSizeLookup = ListaItemSpanSizeLookup(adapter, layoutManager)
        recyclerView.layoutManager = layoutManager
        createItemDecorations(layoutManager).forEach { itemDecoration ->
            recyclerView.addItemDecoration(itemDecoration)
        }
    }

    private fun createItemDecorations(layoutManager: GridLayoutManager): List<ItemDecoration> {
        val boundsDecorationSize = resources.getDimensionPixelSize(
            R.dimen.default_edge_decoration_size
        )
        val defaultDecorationSize = resources.getDimensionPixelOffset(
            R.dimen.default_decoration_size
        )
        return listOf(
            GridSpanMarginDecoration.createHorizontal(
                horizontalMargin = defaultDecorationSize,
                gridLayoutManager = layoutManager,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(
                        viewHolder: RecyclerView.ViewHolder,
                        itemCount: Int
                    ): Boolean {
                        return viewHolder is BigCardRecyclerViewItem.ViewHolder
                    }
                }
            ),
            LinearMarginDecoration(
                topMargin = defaultDecorationSize,
                bottomMargin = defaultDecorationSize,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(
                        viewHolder: RecyclerView.ViewHolder,
                        itemCount: Int
                    ): Boolean {
                        return viewHolder !is SmallCardRecyclerViewItem.ViewHolder
                    }
                }
            ),
            LinearMarginDecoration(
                topMargin = defaultDecorationSize,
                bottomMargin = defaultDecorationSize,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(
                        viewHolder: RecyclerView.ViewHolder,
                        itemCount: Int
                    ): Boolean {
                        return viewHolder is OptionRecyclerViewItem.ViewHolder
                    }
                }
            ),
            LinearMarginDecoration(
                topMargin = boundsDecorationSize,
                bottomMargin = boundsDecorationSize,
                decorationLookup = object : DecorationLookup {
                    override fun shouldApplyDecoration(
                        viewHolder: RecyclerView.ViewHolder,
                        itemCount: Int
                    ): Boolean {
                        return viewHolder is CardListRecyclerViewItem.ViewHolder
                    }
                }
            ),
            LinearBoundsMarginDecoration(
                topMargin = boundsDecorationSize,
                bottomMargin = boundsDecorationSize
            )
        )
    }

}
