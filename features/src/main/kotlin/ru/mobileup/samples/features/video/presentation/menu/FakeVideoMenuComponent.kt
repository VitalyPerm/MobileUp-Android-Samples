package ru.mobileup.samples.features.video.presentation.menu

import ru.mobileup.samples.features.video.domain.VideoOption

class FakeVideoMenuComponent : VideoMenuComponent {
    override fun onVideoOptionChosen(videoOption: VideoOption) = Unit
}