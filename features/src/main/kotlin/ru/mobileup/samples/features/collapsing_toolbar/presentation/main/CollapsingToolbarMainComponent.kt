package ru.mobileup.samples.features.collapsing_toolbar.presentation.main

import ru.mobileup.samples.features.collapsing_toolbar.domain.ToolbarSample

interface CollapsingToolbarMainComponent {

    fun onSampleClick(sample: ToolbarSample)

    sealed interface Output {
        data class SampleChosen(val sample: ToolbarSample) : Output
    }
}
