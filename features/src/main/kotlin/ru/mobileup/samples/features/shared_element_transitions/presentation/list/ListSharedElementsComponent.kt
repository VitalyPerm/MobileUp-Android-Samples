package ru.mobileup.samples.features.shared_element_transitions.presentation.list

import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

interface ListSharedElementsComponent {

    val items: List<ItemSharedElement>

    fun onClickItem(item: ItemSharedElement)

    sealed interface Output {
        data class DetailsRequested(val item: ItemSharedElement) : Output
    }
}