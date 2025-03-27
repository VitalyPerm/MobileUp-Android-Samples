package ru.mobileup.samples.features.shared_element_transitions.presentation.details

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

class RealDetailsSharedElementsComponent(
    componentContext: ComponentContext,
    override val item: ItemSharedElement,
) : ComponentContext by componentContext, DetailsSharedElementsComponent