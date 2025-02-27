package ru.mobileup.samples.features.charts.presentation.widgets

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.common.DrawingContext
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlin.math.ceil
import kotlin.math.floor

private const val Y_STEP = 10.0

val CandlestickRangeProvider = object : CartesianLayerRangeProvider {
    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) =
        Y_STEP * floor(minY / Y_STEP)

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) =
        Y_STEP * ceil(maxY / Y_STEP)
}


fun markerBackground(
    backgroundColor: Color,
    borderColor: Color,
    round: Float,
): Component = object : Component {
    override fun draw(
        context: DrawingContext,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        val rect = RectF(left, top, right, bottom)
        context.canvas.drawRoundRect(
            rect,
            round,
            round,
            Paint().apply {
                this.style = Paint.Style.FILL
                this.color = backgroundColor.toArgb()
            }
        )
        context.canvas.drawRoundRect(
            rect,
            round,
            round,
            Paint().apply {
                this.style = Paint.Style.STROKE
                this.color = borderColor.toArgb()
            }
        )
    }
}