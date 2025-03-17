package ru.mobileup.samples.features.tutorial.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.createTutorialManagementComponent
import ru.mobileup.samples.core.tutorial.domain.Tutorial
import ru.mobileup.samples.core.tutorial.domain.TutorialMessage
import ru.mobileup.samples.core.tutorial.presentation.management.TutorialManagementComponent
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.core.utils.computed
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.tutorial.domain.TutorialFilter

class RealTutorialSampleComponent(
    componentContext: ComponentContext,
    componentFactory: ComponentFactory,
) : ComponentContext by componentContext, TutorialSampleComponent {

    private val tutorial = Tutorial(
        name = "sample",
        listOf(
            TutorialMessage(
                key = TutorialMessageKeys.All,
                text = StringDesc.Resource(R.string.tutorial_text_all)
            ),
            TutorialMessage(
                key = TutorialMessageKeys.First,
                text = StringDesc.Resource(R.string.tutorial_text_first)
            ),
            TutorialMessage(
                key = TutorialMessageKeys.Second,
                text = StringDesc.Resource(R.string.tutorial_text_second)
            ),
            TutorialMessage(
                key = TutorialMessageKeys.Third,
                text = StringDesc.Resource(R.string.tutorial_text_third)
            ),
            TutorialMessage(
                key = TutorialMessageKeys.Back,
                text = StringDesc.Resource(R.string.tutorial_text_back)
            ),
            TutorialMessage(
                key = TutorialMessageKeys.Title,
                text = StringDesc.Resource(R.string.tutorial_text_title)
            )
        )
    )

    override val selectedFilter = MutableStateFlow(TutorialFilter.All)

    override val availableFilters = MutableStateFlow(TutorialFilter.entries)

    private val unfilteredItems = MutableStateFlow(List(100) { it })

    override val items: StateFlow<List<Int>> =
        computed(unfilteredItems, selectedFilter) { items, filter ->
            when (filter) {
                TutorialFilter.All -> items
                TutorialFilter.First -> items.filter { it % 3 == 0 }
                TutorialFilter.Second -> items.filter { it % 3 == 1 }
                TutorialFilter.Third -> items.filter { it % 3 == 2 }
            }
        }

    private val tutorialManagementComponent: TutorialManagementComponent =
        componentFactory.createTutorialManagementComponent(
            childContext("tutorialManagement"),
            tutorial = tutorial
        )

    override fun onFilterSelected(filter: TutorialFilter) {
        selectedFilter.value = filter
    }
}