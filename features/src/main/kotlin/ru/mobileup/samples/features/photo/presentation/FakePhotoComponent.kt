package ru.mobileup.samples.features.photo.presentation

import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.photo.presentation.menu.FakePhotoMenuComponent

class FakePhotoComponent : PhotoComponent {

    override val childStack = createFakeChildStackStateFlow(
        PhotoComponent.Child.Menu(FakePhotoMenuComponent())
    )

    override val backHandler = fakeBackHandler

    override fun onBackClick() = Unit
}
