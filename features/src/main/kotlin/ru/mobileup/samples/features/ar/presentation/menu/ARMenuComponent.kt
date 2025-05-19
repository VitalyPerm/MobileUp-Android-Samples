package ru.mobileup.samples.features.ar.presentation.menu

interface ARMenuComponent {
    fun onPlacementClick()

    sealed interface Output {
        data object PlacementRequested : Output
    }
}
