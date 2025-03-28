package ru.mobileup.samples.features.shared_element_transitions.presentation.list

import ru.mobileup.samples.features.shared_element_transitions.data.ItemSharedElementItems
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

class FakeListSharedElementsComponent : ListSharedElementsComponent {
    override val items: List<ItemSharedElement> = ItemSharedElementItems.items

    override fun onClickItem(item: ItemSharedElement) = Unit
}