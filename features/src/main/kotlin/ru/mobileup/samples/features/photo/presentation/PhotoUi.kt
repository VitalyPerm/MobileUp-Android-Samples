package ru.mobileup.samples.features.photo.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.photo.presentation.camera.PhotoCameraUi
import ru.mobileup.samples.features.photo.presentation.menu.PhotoMenuUi
import ru.mobileup.samples.features.photo.presentation.preview.PhotoPreviewUi

@Composable
fun PhotoUi(
    component: PhotoComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.collectAsState()

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)

    Children(
        stack = childStack,
        animation = stackAnimation(slide()),
        modifier = modifier
    ) { child ->
        when (val instance = child.instance) {
            is PhotoComponent.Child.Menu -> PhotoMenuUi(instance.component)
            is PhotoComponent.Child.Camera -> PhotoCameraUi(instance.component)
            is PhotoComponent.Child.Preview -> PhotoPreviewUi(instance.component)
        }
    }
}

@Composable
private fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview
@Composable
private fun VideoUiPreview() {
    AppTheme {
        PhotoUi(FakePhotoComponent())
    }
}