package ru.mobileup.samples.features.shared_element_transitions.presentation.list

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.features.shared_element_transitions.data.ItemSharedElementItems
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

class RealListSharedElementsComponent(
    componentContext: ComponentContext,
    private val onOutput: (ListSharedElementsComponent.Output) -> Unit
) : ComponentContext by componentContext, ListSharedElementsComponent {

    override val items: List<ItemSharedElement> = ItemSharedElementItems.items

    override fun onClickItem(item: ItemSharedElement) {
        onOutput(ListSharedElementsComponent.Output.DetailsRequested(item))
    }
}