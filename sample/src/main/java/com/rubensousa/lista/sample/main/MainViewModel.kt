package com.rubensousa.lista.sample.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubensousa.lista.item.ListaItem
import com.rubensousa.lista.sample.R
import com.rubensousa.lista.sample.model.*
import com.rubensousa.lista.sample.ui.BigCardItem
import com.rubensousa.lista.sample.ui.SmallCardItem
import com.rubensousa.lista.sample.ui.CardListItem
import com.rubensousa.lista.sample.ui.HeaderItem
import com.rubensousa.lista.sample.ui.OptionItem
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val listItems = MutableLiveData<List<ListaItem<Any>>>()

    init {
        viewModelScope.launch {
            loadItems()
        }
    }

    fun getListItems(): LiveData<List<ListaItem<Any>>> = listItems

    fun loadItems() {
        val list = arrayListOf<ListaItem<*>>()
        val headers = listOf(
            R.string.header_one, R.string.header_two, R.string.header_three,
            R.string.header_four, R.string.header_five, R.string.header_six
        )
        val cards = arrayListOf<SmallCardItem>()
        repeat(20) {
            cards.add(SmallCardItem(CardModel(it)))
        }
        headers.forEach { headerTitle ->
            list.add(HeaderItem(HeaderModel(titleResource = headerTitle)))
            list.add(
                OptionItem(
                    OptionModel(
                        id = list.size,
                        titleResource = R.string.option_one,
                        subtitleResource = R.string.option_subtitle
                    ),
                    onClick = {

                    }
                )
            )
            list.add(BigCardItem(CardModel(list.size)))
            list.add(BigCardItem(CardModel(list.size)))
            list.add(
                OptionItem(
                    OptionModel(
                        id = list.size,
                        titleResource = R.string.option_two,
                        subtitleResource = R.string.option_subtitle
                    ),
                    onClick = {

                    }
                )
            )
            list.add(CardListItem(CardListModel(id = list.size, items = ArrayList(cards))))
        }
        listItems.postValue(list)
    }

}