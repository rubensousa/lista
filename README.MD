# Lista

Lista helps building composable sections and nested lists in RecyclerViews.
It also comes with useful Espresso extensions that help you build UI tests that perform actions in lists.

## Install

```groovy
 implementation 'com.rubensousa.lista:lista:1.0.0'
 
 // Optional Espresso test extensions
 androidTestImplementation 'com.rubensousa.lista:lista-testing:1.0.0'
```

Replace x.x.x with the latest version available


## Basic Setup

1. Define a ListaSection. Example:

```kotlin
class CardSection : ListaSection<CardModel>(R.layout.section_card) {

    /**
     * @return the ViewHolder to be used by this Section
     */
    override fun onCreateViewHolder(view: View) : ListaSectionViewHolder<CardModel> {
        return VH(view)
    }

    /**
     * @return true if this Section should be used to bind [item] or false otherwise
     */
    override fun isForItem(item: Any): Boolean = item is CardModel

    class VH(view: View) :  ListaSectionViewHolder<CardModel>(view) {

        private val binding = SectionCardBinding.bind(view)

        /**
         * Called after onBindViewHolder
         */
        override fun onBind(item: CardModel) {
            super.onBind(item)
            binding.title.text = item.title
            binding.title.isVisible = item.showTitle
        }
    }

}
```

2. Create a ListaAdapter and register your sections:

```kotlin
val diffItemCallback = object : DiffUtil.ItemCallback<SectionModel>() {

    override fun areItemsTheSame(oldItem: SectionModel, newItem: SectionModel): Boolean {
        return oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItem: SectionModel, newItem: SectionModel): Boolean {
        return oldItem.equals(newItem)
    }

}

val adapter = ListaAdapter(diffItemCallback)
adapter.addSection(CardSection())
```

3. Use `submitList` to dispatch items to the adapter:

```kotlin
val adapterContentList = listOf(
    CardModel(title = "test", titleVisible = true)
)
adapter.submitList(adapterContentList)
```

This will apply diffing automatically and call the appropriate `notify` methods.


## Nested lists

If you want to add a section that contains a nested list, you can extend your section from `ListaNestedSection`
and/or your ViewHolder from `ListaNestedSectionViewHolder`

A `ListaNestedSection` contains a `RecyclerView.RecycledViewPool` for re-using ViewHolders across different lists
and a `ListaScrollStateManager` to persist their scroll state.


```kotlin
class CardListSection : ListaNestedSection<CardListModel>(R.layout.section_card_list) {

    override fun onCreateViewHolder(view: View): ListaSectionViewHolder<CardListModel> {
        // Uses the default recycledViewPool and scrollStateManager
        return VH(view, recycledViewPool, scrollStateManager)
    }

    override fun isForItem(item: Any): Boolean = item is CardListModel

    class VH(
        view: View,
        recycledViewPool: RecyclerView.RecycledViewPool,
        scrollStateManager: ListaScrollStateManager
    ) : ListaNestedSectionViewHolder<CardListModel>(view, recycledViewPool, scrollStateManager) {

        private val binding = SectionCardListBinding.bind(view)
        private val adapter = ListaAdapter(ListModelDiffCallback())

        override fun onCreated() {
            super.onCreated()
            adapter.addSection(CardSection())
            val layoutManager = LinearLayoutManager(
                binding.cardRecyclerView.context, RecyclerView.HORIZONTAL, false
            )
            layoutManager.recycleChildrenOnDetach = true
            binding.cardRecyclerView.layoutManager = layoutManager
            binding.cardRecyclerView.adapter = adapter
        }

        override fun updateAdapter(item: CardListModel) {
            adapter.submitList(item.items, applyDiffing = false)
        }
        
        override fun getRecyclerView(): RecyclerView = binding.cardRecyclerView

        override fun getAdapter(): RecyclerView.Adapter<*> = adapter

        override fun getScrollStateKey(item: CardListModel): String = item.getId()

    }

}
```


## GridLayoutManager span lookup

`ListaSpanLookup` can be used to provide different span sizes for each section.

First, create a `ListaSpanLookup` by passing the adapter and a default span size:

```kotlin
val layoutManager = recyclerView.layoutManager as GridLayoutManager
val spanSizeLookup = ListaSpanLookup(adapter, defaultSpanSize = layoutManager.spanCount)
layoutManager.spanSizeLookup = spanSizeLookup
```

Now you can register each span size with `setSpanSizeForSection`:
```kotlin
spanSizeLookup.setSpanSizeForSection(cardSection, 1)
```

## Testing

The `lista-testing` artifact provides useful Espresso extensions for testing RecyclerViews.

### Actions

`ListaActions` provides the following:

 - `smoothScrollTo` - smooth scrolls a RecyclerView to a position
 - `waitForItemViewLayout` - waits until a RecyclerView item view has been laid out

### Assertions

`ListaAssertions` provides the following:

- `withItemCount` - asserts the item count of the adapter

### Matchers

`ListaNestedMatchers` provides the following:

- `withAscendant` - matches a View with an ascendant that matches a `Matcher`
- `withNestedRecyclerViewInSection` - matches a RecyclerView inside a ViewHolder
- `withNestedRecyclerView` - similar to `withNestedRecyclerViewInSection` but assumes the RecyclerView is the ViewHolder's itemView.
- `withNestedView` - matches a View inside a nested RecyclerView
- `withNestedChildView` - matches a child View of a View that's inside a nested RecyclerView



Check the sample app for a complete example of integration of the library.

## License

    Copyright 2022 Rúben Sousa
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

