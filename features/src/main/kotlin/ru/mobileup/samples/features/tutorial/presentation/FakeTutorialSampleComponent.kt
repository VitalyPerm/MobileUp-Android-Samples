package ru.mobileup.samples.features.tutorial.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.tutorial.domain.TutorialFilter
import ru.mobileup.samples.core.tutorial.presentation.management.FakeTutorialManagementComponent
import ru.mobileup.samples.core.tutorial.presentation.management.TutorialManagementComponent

class FakeTutorialSampleComponent : TutorialSampleComponent {

    override val items: StateFlow<List<Int>> =
        MutableStateFlow(List(5) { it })

    override val selectedFilter: StateFlow<TutorialFilter> =
        MutableStateFlow(TutorialFilter.All)

    override val availableFilters: StateFlow<List<TutorialFilter>> =
        MutableStateFlow(TutorialFilter.entries
        )
    override val tutorialManagementComponent: TutorialManagementComponent =
        FakeTutorialManagementComponent()

    override fun onFilterSelected(filter: TutorialFilter) = Unit
}