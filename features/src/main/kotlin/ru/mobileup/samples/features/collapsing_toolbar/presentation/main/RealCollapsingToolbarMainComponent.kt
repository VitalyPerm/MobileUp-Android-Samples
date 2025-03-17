package ru.mobileup.samples.features.collapsing_toolbar.presentation.main

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.features.collapsing_toolbar.domain.ToolbarSample

class RealCollapsingToolbarMainComponent(
    componentContext: ComponentContext,
    private val onOutput: (CollapsingToolbarMainComponent.Output) -> Unit,
) : ComponentContext by componentContext, CollapsingToolbarMainComponent {

    override fun onSampleClick(sample: ToolbarSample) =
        onOutput(CollapsingToolbarMainComponent.Output.SampleChosen(sample))
}
