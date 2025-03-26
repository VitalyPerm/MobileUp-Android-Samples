package ru.mobileup.samples.features.shared_element_transitions

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement
import ru.mobileup.samples.features.shared_element_transitions.presentation.RealSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.presentation.SharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.presentation.details.DetailsSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.presentation.details.RealDetailsSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.presentation.list.ListSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.presentation.list.RealListSharedElementsComponent

fun ComponentFactory.createSharedElementsComponent(
    componentContext: ComponentContext,
): SharedElementsComponent {
    return RealSharedElementsComponent(
        componentContext,
        get()
    )
}

fun ComponentFactory.createListSharedElementsComponent(
    componentContext: ComponentContext,
    onOutput: (ListSharedElementsComponent. Output) -> Unit
): ListSharedElementsComponent {
    return RealListSharedElementsComponent(
        componentContext,
        onOutput
    )
}

fun ComponentFactory.createDetailsSharedElementsComponent(
    componentContext: ComponentContext,
    item: ItemSharedElement,
): DetailsSharedElementsComponent {
    return RealDetailsSharedElementsComponent(
        componentContext,
        item
    )
}