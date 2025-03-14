package ru.mobileup.samples.features.tutorial.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.tutorial.domain.TutorialFilter
import ru.mobileup.samples.core.tutorial.presentation.management.TutorialManagementComponent

interface TutorialSampleComponent {
    val items: StateFlow<List<Int>>

    val selectedFilter: StateFlow<TutorialFilter>
    val availableFilters: StateFlow<List<TutorialFilter>>

    val tutorialManagementComponent: TutorialManagementComponent

    fun onFilterSelected(filter: TutorialFilter)
}