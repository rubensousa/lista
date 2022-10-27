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

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.rubensousa.lista.ListaSection
import com.rubensousa.lista.ListaViewHolder
import com.rubensousa.lista.viewHolderBinding
import com.rubensousa.lista.sample.databinding.SectionOptionBinding
import com.rubensousa.lista.sample.model.OptionModel

class OptionSection(private val onOptionClickListener: OnOptionClickListener) :
    ListaSection<OptionModel, OptionSection.ViewHolder>() {

    interface OnOptionClickListener {
        fun onOptionClicked(optionModel: OptionModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.viewHolderBinding(SectionOptionBinding::inflate))
    }

    override fun onViewHolderBound(holder: ViewHolder, item: OptionModel, payloads: List<Any>) {
        super.onViewHolderBound(holder, item, payloads)
        holder.clickListener = onOptionClickListener
    }

    override fun onViewHolderRecycled(holder: ViewHolder) {
        super.onViewHolderRecycled(holder)
        holder.clickListener = null
    }

    class ViewHolder(private val binding: SectionOptionBinding) :
        ListaViewHolder<OptionModel>(binding.root) {

        var clickListener: OnOptionClickListener? = null

        override fun onCreated() {
            super.onCreated()
            itemView.setOnClickListener {
                getItem()?.let { item -> clickListener?.onOptionClicked(item) }
            }
        }

        override fun onBound(item: OptionModel, payloads: List<Any>) {
            super.onBound(item, payloads)
            binding.optionTitleTextView.setText(item.titleResource)
            if (item.subtitleResource == null) {
                binding.optionSubtitleTextView.text = ""
                binding.optionSubtitleTextView.isVisible = false
            } else {
                binding.optionSubtitleTextView.isVisible = true
                binding.optionSubtitleTextView.setText(item.subtitleResource)
            }
        }


    }

}
