package ru.mobileup.samples.features.qr_code.presentation.generator

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrCodeShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.hexagon
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.options.square
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.qr_code.domain.QrCode

@Composable
fun QrCodeGeneratorUi(
    component: QrCodeGeneratorComponent,
    modifier: Modifier = Modifier
) {
    val link by component.link.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .systemBarsPadding()
            .imePadding()
    ) {
        QrCodeView(link)
    }
}

@Composable
private fun QrCodeView(
    link: QrCode,
    modifier: Modifier = Modifier
) {
    val logoPainter = painterResource(R.drawable.ic_app_logo)

    val darkGradientColors = listOf(
        CustomTheme.colors.button.primary,
        CustomTheme.colors.border.error,
    )
    val ballColor = CustomTheme.colors.icon.primary

    val painter = rememberQrCodePainter(data = link.value) {
        logo {
            painter = logoPainter
            padding = QrLogoPadding.Natural(0.02f)
            shape = QrLogoShape.circle()
            size = 0.3f
        }

        shapes {
            pattern = QrCodeShape.hexagon()
            frame = QrFrameShape.roundCorners(corner = 2f)
            ball = QrBallShape.circle()

            darkPixel = QrPixelShape.circle()
            lightPixel = QrPixelShape.square()
        }

        colors {
            dark = QrBrush.brush { size ->
                val (startTop, endBottom) = darkGradientColors
                Brush.linearGradient(
                    0f to startTop,
                    1f to endBottom,
                    end = Offset(size, size)
                )
            }
            ball = QrBrush.solid(ballColor)
        }
    }

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(300.dp)
    )
}

@Preview
@Composable
private fun QrCodeGeneratorUiPreview() {
    AppTheme {
        QrCodeGeneratorUi(FakeQrCodeGeneratorComponent())
    }
}
