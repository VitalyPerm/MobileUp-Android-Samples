package ru.mobileup.samples.features.photo.presentation.menu

interface PhotoMenuComponent {

    fun onCameraClick()

    sealed interface Output {
        data object CameraRequested : Output
    }
}