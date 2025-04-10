package ru.mobileup.samples.features.photo.presentation

import androidx.activity.OnBackPressedDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.photo.presentation.menu.FakePhotoMenuComponent

class FakePhotoComponent : PhotoComponent {

    override val childStack = createFakeChildStackStateFlow(
        PhotoComponent.Child.Menu(FakePhotoMenuComponent())
    )

    override val backHandler = BackHandler(OnBackPressedDispatcher())

    override fun onBackClick() = Unit
}
