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

import androidx.recyclerview.widget.DiffUtil
import com.rubensousa.lista.sample.model.SectionModel

class ListModelDiffCallback : DiffUtil.ItemCallback<SectionModel>() {

    override fun areItemsTheSame(oldItem: SectionModel, newItem: SectionModel): Boolean {
        return oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItem: SectionModel, newItem: SectionModel): Boolean {
        return oldItem.equals(newItem)
    }

}
