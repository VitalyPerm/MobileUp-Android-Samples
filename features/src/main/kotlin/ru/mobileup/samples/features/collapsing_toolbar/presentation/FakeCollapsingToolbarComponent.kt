package ru.mobileup.samples.features.collapsing_toolbar.presentation

import androidx.activity.OnBackPressedDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.FakeCollapsingToolbarMainComponent

class FakeCollapsingToolbarComponent : CollapsingToolbarComponent {

    override val stack = createFakeChildStackStateFlow(
        CollapsingToolbarComponent.Child.Main(FakeCollapsingToolbarMainComponent())
    )

    override fun onBackClick() = Unit

    override val backHandler = BackHandler(OnBackPressedDispatcher())
}
