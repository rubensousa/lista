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

package com.rubensousa.lista.sample.sections

import android.view.View
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaSectionViewHolder
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.databinding.SectionHeaderBinding
import com.rubensousa.lista.sample.model.HeaderModel

class HeaderSection : ListaSection<HeaderModel>(R.layout.section_header) {

    override fun onCreateViewHolder(view: View): ListaSectionViewHolder<HeaderModel> = VH(view)
    
    class VH(view: View) : ListaSectionViewHolder<HeaderModel>(view) {

        private val binding = SectionHeaderBinding.bind(view)

        override fun onBind(item: HeaderModel, payloads: List<Any>) {
            super.onBind(item, payloads)
            binding.headerTitleTextView.setText(item.titleResource)
        }

    }

}
