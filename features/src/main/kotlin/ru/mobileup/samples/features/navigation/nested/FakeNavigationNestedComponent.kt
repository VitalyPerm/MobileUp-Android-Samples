package ru.mobileup.samples.features.navigation.nested

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.navigation.nested.NavigationNestedComponent.Child
import ru.mobileup.samples.features.navigation.nested.main.FakeNavigationNestedMainComponent

class FakeNavigationNestedComponent : NavigationNestedComponent {

    override val stack = createFakeChildStackStateFlow(
        Child.Main(FakeNavigationNestedMainComponent())
    )

    override val isBottomBarVisible = MutableStateFlow(false)

    override val backHandler = fakeBackHandler

    override fun onBackClick() = Unit
}
