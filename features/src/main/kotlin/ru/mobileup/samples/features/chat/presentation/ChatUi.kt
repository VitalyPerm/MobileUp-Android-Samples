package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.SystemBarIconsColor
import ru.mobileup.samples.core.utils.SystemBars
import ru.mobileup.samples.core.utils.systemBarsWithImePadding
import ru.mobileup.samples.core.widget.text_field.AppTextField
import ru.mobileup.samples.core.widget.text_field.AppTextFieldDefaults
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.MessageAuthor
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatUi(
    component: ChatComponent,
    modifier: Modifier = Modifier,
) {
    SystemBars(
        statusBarColor = CustomTheme.colors.button.primaryDisabled,
        navigationBarColor = CustomTheme.colors.button.primaryDisabled,
        statusBarIconsColor = SystemBarIconsColor.Light,
        navigationBarIconsColor = SystemBarIconsColor.Light
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar()
        }
    ) { paddingValues ->
        ChatComponent(
            component = component,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CustomTheme.colors.button.primaryDisabled)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = "logo",
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "Test",
            color = CustomTheme.colors.palette.white,
            modifier = Modifier
                .weight(2f)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ChatComponent(
    component: ChatComponent,
    modifier: Modifier = Modifier
) {
    val chatState = component.chatState.collectAsState()
    val state = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        component.effectFlow.onEach { effect ->
            when (effect) {
                is ExternalChatEffect.ReactToAddedMessages -> {
                    state.requestScrollToItem(state.layoutInfo.totalItemsCount)
                }

                else -> {
                    // Do nothing
                }
            }
        }.flowOn(Dispatchers.Default).launchIn(this)
    }

    Box(
        modifier = modifier
            .systemBarsWithImePadding()
            .background(CustomTheme.colors.background.screen)
    ) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(state = state) {
                    items(
                        items = chatState.value.chatMessagesState.messages,
                        key = { it.id.value }
                    ) { message ->
                        MessageContent(message)
                    }

                    items(
                        items = chatState.value.chatMessagesState.pendingMessages,
                        key = { it.id.value }
                    ) { message ->
                        MessageContent(message)
                    }
                }
            }

            InputLayout(
                inputControl = component.inputControl,
                onSendMessage = component::onSendMessage,
                onSendFile = component::onSendFile
            )
        }
    }
}

@Composable
private fun MessageContent(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val direction by remember(message) {
        derivedStateOf {
            if (message.author is MessageAuthor.Me) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }
        }
    }

    val time by remember(message.time) {
        derivedStateOf {
            message.time.format(
                DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
            )
        }
    }

    val originDirection = LocalLayoutDirection.current

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides originDirection) {
                Row(
                    modifier = Modifier
                        .padding(
                            if (message.author is MessageAuthor.Me) {
                                PaddingValues(end = 8.dp)
                            } else {
                                PaddingValues(start = 8.dp)
                            }
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(CustomTheme.colors.button.primary)
                        .padding(all = 8.dp)
                ) {
                    Text(
                        text = message.text,
                        style = CustomTheme.typography.body.regular,
                        color = CustomTheme.colors.text.invert,
                        modifier = Modifier.widthIn(max = 250.dp)
                    )

                    Icon(
                        painter = painterResource(
                            if (message.messageStatus is MessageStatus.Failed) {
                                R.drawable.ic_error
                            } else {
                                R.drawable.ic_done
                            }
                        ),
                        tint = when (message.messageStatus) {
                            MessageStatus.Sending -> CustomTheme.colors.palette.white50
                            MessageStatus.Sent -> CustomTheme.colors.palette.white
                            is MessageStatus.Failed -> Color.Unspecified
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                            .align(Alignment.Bottom)
                    )
                }
            }

            Text(
                text = time,
                style = CustomTheme.typography.caption.regular,
                color = CustomTheme.colors.text.secondary,
                modifier = Modifier.align(Alignment.Bottom)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun InputLayout(
    inputControl: InputControl,
    onSendMessage: () -> Unit,
    onSendFile: (Uri) -> Unit
) {
    val value = inputControl.value.collectAsState()

    val pickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onSendFile(it)
            }
        }

    AppTextField(
        modifier = Modifier.fillMaxWidth(),
        inputControl = inputControl,
        placeholder = "Message",
        colors = AppTextFieldDefaults.colors.copy(
            focusedContainerColor = CustomTheme.colors.button.primaryDisabled,
            unfocusedContainerColor = CustomTheme.colors.button.primaryDisabled
        ),
        visualTransformation = inputControl.visualTransformation,
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .clickable {
                        pickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    }
                    .padding(8.dp),
                painter = painterResource(
                    R.drawable.ic_attach
                ),
                tint = CustomTheme.colors.icon.primary,
                contentDescription = null
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = value.value.isNotEmpty(),
                enter = expandHorizontally(
                    expandFrom = Alignment.End
                ),
                exit = fadeOut()
            ) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            onSendMessage()
                        }
                        .padding(8.dp),
                    painter = painterResource(
                        R.drawable.ic_send
                    ),
                    tint = CustomTheme.colors.icon.primary,
                    contentDescription = null
                )
            }
        },
        shape = RectangleShape
    )
}

@Preview
@Composable
private fun ChatUiPreview() {
    AppTheme {
        ChatUi(FakeChatComponent())
    }
}