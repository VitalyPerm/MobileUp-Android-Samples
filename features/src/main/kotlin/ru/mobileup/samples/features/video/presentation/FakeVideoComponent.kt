package ru.mobileup.samples.features.video.presentation

import androidx.activity.OnBackPressedDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.video.presentation.menu.FakeVideoMenuComponent

class FakeVideoComponent() : VideoComponent {

    override val childStack = createFakeChildStackStateFlow(
        VideoComponent.Child.Menu(FakeVideoMenuComponent())
    )

    override val backHandler = BackHandler(OnBackPressedDispatcher())

    override fun onBackClick() = Unit
}
