package ru.mobileup.samples.features.charts.presentation.widgets.circleChart

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Stable
class CircleChartDataProducer(
    initialData: CircleChartData
) {
    private val _data = MutableStateFlow(initialData)
    val data = _data.asStateFlow()

    suspend fun updateChartData(data: CircleChartData) {
        _data.emit(data)
    }
}

@Immutable
data class CircleChartData(
    val slices: List<Slice>
) {
    private val total: Float = slices.map { it.value }.sum()

    data class Slice(
        val value: Float,
        val label: String
    )

    fun sweepAngles(): List<Float> {
        return slices.map { (it.value / total) * 360 }
    }
}