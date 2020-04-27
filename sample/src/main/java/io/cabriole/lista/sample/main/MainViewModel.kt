package io.cabriole.lista.sample.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.cabriole.lista.sample.R
import io.cabriole.lista.sample.model.*

class MainViewModel : ViewModel() {

    private val items = MutableLiveData<List<SectionModel>>()

    fun getItems(): LiveData<List<SectionModel>> = items

    fun loadItems() {
        if (items.value == null) {
            val list = arrayListOf<SectionModel>()
            val headers = listOf(
                R.string.header_one, R.string.header_two, R.string.header_three,
                R.string.header_four, R.string.header_five, R.string.header_six
            )
            val cards = arrayListOf<CardModel>()
            repeat(20) {
                cards.add(CardModel(it))
            }

            headers.forEach { headerTitle ->
                list.add(HeaderModel(titleResource = headerTitle))
                list.add(
                    OptionModel(
                        id = list.size,
                        titleResource = R.string.option_one,
                        subtitleResource = R.string.option_subtitle
                    )
                )
                list.add(CardModel(list.size))
                list.add(CardModel(list.size))
                list.add(
                    OptionModel(
                        id = list.size,
                        titleResource = R.string.option_two,
                        subtitleResource = R.string.option_subtitle
                    )
                )
                list.add(CardListModel(id = list.size, items = ArrayList(cards)))
            }
            items.postValue(list)
        }
    }

}