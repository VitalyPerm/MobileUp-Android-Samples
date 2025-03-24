package ru.mobileup.samples.features.root.presentation

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.createMessageComponent
import ru.mobileup.samples.core.createTutorialOverlayComponent
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.calendar.createCalendarComponent
import ru.mobileup.samples.features.charts.createChartComponent
import ru.mobileup.samples.features.collapsing_toolbar.createCollapsingToolbarComponent
import ru.mobileup.samples.features.form.createFormComponent
import ru.mobileup.samples.features.image.createImageComponent
import ru.mobileup.samples.features.menu.createMenuComponent
import ru.mobileup.samples.features.menu.domain.Sample
import ru.mobileup.samples.features.menu.presentation.MenuComponent
import ru.mobileup.samples.features.navigation.createNavigationComponent
import ru.mobileup.samples.features.otp.createOtpComponent
import ru.mobileup.samples.features.otp.presentation.OtpComponent
import ru.mobileup.samples.features.qr_code.createQrCodeComponent
import ru.mobileup.samples.features.root.presentation.RootComponent.Child.*
import ru.mobileup.samples.features.tutorial.createTutorialSampleComponent
import ru.mobileup.samples.features.video.createVideoComponent
import ru.mobileup.samples.features.yandex_map.createYandexMapComponent

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Menu,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override val messageComponent = componentFactory.createMessageComponent(
        childContext(key = "message")
    )

    override val tutorialOverlayComponent = componentFactory.createTutorialOverlayComponent(
        childContext("tutorialOverlay")
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext,
    ): RootComponent.Child = when (config) {
        ChildConfig.Menu -> {
            Menu(
                componentFactory.createMenuComponent(componentContext, ::onMenuOutput)
            )
        }

        ChildConfig.Form -> {
            Form(
                componentFactory.createFormComponent(componentContext)
            )
        }

        ChildConfig.Otp -> {
            Otp(
                componentFactory.createOtpComponent(componentContext, ::onOtpOutput)
            )
        }

        ChildConfig.Video -> {
            Video(
                componentFactory.createVideoComponent(componentContext)
            )
        }

        ChildConfig.Calendar -> {
            Calendar(
                componentFactory.createCalendarComponent(componentContext)
            )
        }

        ChildConfig.QrCode -> {
            QrCode(
                componentFactory.createQrCodeComponent(componentContext)
            )
        }

        ChildConfig.Chart -> {
            Chart(
                componentFactory.createChartComponent(componentContext)
            )
        }

        ChildConfig.Navigation -> {
            Navigation(
                componentFactory.createNavigationComponent(componentContext)
            )
        }

        ChildConfig.CollapsingToolbar -> {
            CollapsingToolbar(
                componentFactory.createCollapsingToolbarComponent(componentContext)
            )
        }

        ChildConfig.Image -> {
            Image(
                componentFactory.createImageComponent(componentContext)
            )
        }

        ChildConfig.Tutorial -> {
            Tutorial(
                componentFactory.createTutorialSampleComponent(componentContext)
            )
        }

        ChildConfig.YandexMap -> {
            YandexMap(
                componentFactory.createYandexMapComponent(componentContext)
            )
        }
    }

    private fun onMenuOutput(output: MenuComponent.Output) {
        when (output) {
            is MenuComponent.Output.SampleChosen -> navigation.safePush(
                when (output.sample) {
                    Sample.Form -> ChildConfig.Form
                    Sample.Otp -> ChildConfig.Otp
                    Sample.Video -> ChildConfig.Video
                    Sample.Calendar -> ChildConfig.Calendar
                    Sample.QrCode -> ChildConfig.QrCode
                    Sample.Chart -> ChildConfig.Chart
                    Sample.Navigation -> ChildConfig.Navigation
                    Sample.CollapsingToolbar -> ChildConfig.CollapsingToolbar
                    Sample.Image -> ChildConfig.Image
                    Sample.Tutorial -> ChildConfig.Tutorial
                    Sample.YandexMap -> ChildConfig.YandexMap
                }
            )
        }
    }

    private fun onOtpOutput(output: OtpComponent.Output) {
        when (output) {
            OtpComponent.Output.OtpSuccessfullyVerified -> navigation.pop()
        }
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object Menu : ChildConfig

        @Serializable
        data object Form : ChildConfig

        @Serializable
        data object Otp : ChildConfig

        @Serializable
        data object Video : ChildConfig

        @Serializable
        data object Calendar : ChildConfig

        @Serializable
        data object QrCode : ChildConfig

        @Serializable
        data object Chart : ChildConfig

        @Serializable
        data object Navigation : ChildConfig

        @Serializable
        data object CollapsingToolbar : ChildConfig

        @Serializable
        data object Image : ChildConfig

        @Serializable
        data object Tutorial : ChildConfig

        @Serializable
        data object YandexMap : ChildConfig
    }
}
