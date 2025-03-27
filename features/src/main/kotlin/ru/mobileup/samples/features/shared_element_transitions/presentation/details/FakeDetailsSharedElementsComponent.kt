package ru.mobileup.samples.features.shared_element_transitions.presentation.details

import ru.mobileup.samples.features.shared_element_transitions.data.ItemSharedElementItems
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

class FakeDetailsSharedElementsComponent : DetailsSharedElementsComponent {
    override val item: ItemSharedElement = ItemSharedElementItems.items.first()
}