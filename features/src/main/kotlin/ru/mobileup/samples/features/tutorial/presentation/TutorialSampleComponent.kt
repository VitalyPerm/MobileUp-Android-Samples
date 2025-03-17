package ru.mobileup.samples.features.tutorial.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.tutorial.domain.TutorialFilter

interface TutorialSampleComponent {
    val items: StateFlow<List<Int>>

    val selectedFilter: StateFlow<TutorialFilter>
    val availableFilters: StateFlow<List<TutorialFilter>>

    fun onFilterSelected(filter: TutorialFilter)
}