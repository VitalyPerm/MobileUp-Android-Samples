package ru.mobileup.samples.features.document.presentation

import androidx.activity.OnBackPressedDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.document.presentation.menu.FakeDocumentMenuComponent

class FakeDocumentComponent : DocumentComponent {

    override val childStack = createFakeChildStackStateFlow(
        DocumentComponent.Child.Menu(FakeDocumentMenuComponent())
    )

    override val backHandler = BackHandler(OnBackPressedDispatcher())

    override fun onBackClick() = Unit
}
