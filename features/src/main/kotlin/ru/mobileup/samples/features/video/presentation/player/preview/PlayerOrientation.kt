package ru.mobileup.samples.features.video.presentation.player.preview

sealed interface PlayerOrientation {
    data object Landscape : PlayerOrientation
    data object Portrait : PlayerOrientation
}