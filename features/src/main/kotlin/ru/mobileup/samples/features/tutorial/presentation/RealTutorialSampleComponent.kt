package ru.mobileup.samples.features.tutorial.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.createTutorialManagementComponent
import ru.mobileup.samples.core.tutorial.domain.Tutorial
import ru.mobileup.samples.core.tutorial.domain.TutorialMessage
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.core.utils.computed
import ru.mobileup.samples.features.tutorial.domain.TutorialFilter

class RealTutorialSampleComponent(
    componentContext: ComponentContext,
    componentFactory: ComponentFactory,
) : ComponentContext by componentContext, TutorialSampleComponent {

    override val selectedFilter = MutableStateFlow(TutorialFilter.All)

    override val availableFilters = MutableStateFlow(TutorialFilter.entries)

    private val tutorial = Tutorial(
        name = "sample",
        messages = TutorialMessageKeys.entries.map {
            TutorialMessage(key = it, text = it.message)
        }
    )

    private val tutorialManagementComponent = componentFactory.createTutorialManagementComponent(
        componentContext = childContext("tutorialManagement"),
        tutorial = tutorial
    )

    private val backCallback = BackCallback {
        tutorialManagementComponent.tutorialManager.skipTutorial()
    }

    init {
        backHandler.register(backCallback)

        tutorialManagementComponent.tutorialManager.visibleMessage.onEach {
            backCallback.isEnabled = it != null
        }.launchIn(componentScope)
    }

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

    override fun onFilterSelected(filter: TutorialFilter) {
        selectedFilter.value = filter
    }
}