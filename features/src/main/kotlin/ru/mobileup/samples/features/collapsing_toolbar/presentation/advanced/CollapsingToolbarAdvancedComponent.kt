package ru.mobileup.samples.features.collapsing_toolbar.presentation.advanced

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext

interface CollapsingToolbarAdvancedComponent

class RealCollapsingToolbarAdvancedComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, CollapsingToolbarAdvancedComponent

@Composable
fun CollapsingToolbarAdvancedUi(
    component: CollapsingToolbarAdvancedComponent,
    modifier: Modifier = Modifier
) {}
