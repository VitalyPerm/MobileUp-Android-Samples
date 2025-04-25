package ru.mobileup.samples.features.chat.presentation.widget

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.clickableNoRipple
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.message.DownloadingStatus
import ru.mobileup.samples.features.chat.domain.state.message.MessageAuthor
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onMessageClick: () -> Unit,
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
                        .run {
                            if (message.attachment == null) {
                                this
                                    .clickable {
                                        onMessageClick()
                                    }
                                    .background(
                                        if (message.author is MessageAuthor.Me) {
                                            CustomTheme.colors.chat.secondary
                                        } else {
                                            CustomTheme.colors.chat.tertiary
                                        }
                                    )
                                    .padding(all = 8.dp)
                            } else {
                                this
                            }
                        }
                ) {
                    Text(
                        text = message.text,
                        style = CustomTheme.typography.body.regular,
                        color = CustomTheme.colors.text.primary,
                        modifier = Modifier.widthIn(max = 250.dp)
                    )

                    message.attachment?.let { attachment ->
                        MessageAttachment(
                            attachment = attachment,
                            messageStatus = message.messageStatus,
                            onMessageClick = onMessageClick
                        )
                    }

                    Icon(
                        painter = painterResource(
                            if (message.messageStatus is MessageStatus.Failed) {
                                R.drawable.ic_error
                            } else {
                                R.drawable.ic_done
                            }
                        ),
                        tint = when (message.messageStatus) {
                            MessageStatus.Sending -> CustomTheme.colors.icon.primary.copy(alpha = 0.1f)
                            MessageStatus.Sent -> CustomTheme.colors.icon.primary.copy(alpha = 0.5f)
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
private fun MessageAttachment(
    attachment: ChatAttachment,
    messageStatus: MessageStatus,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        if (attachment.type == ChatAttachment.Type.FILE) {
            DocumentAttachment(
                name = attachment.filename,
                messageStatus = messageStatus,
                downloadingStatus = attachment.downloadingStatus,
                onMessageClick = onMessageClick
            )
        } else {
            MediaAttachment(
                name = attachment.filename,
                localPath = attachment.localFilePath,
                messageStatus = messageStatus,
                downloadingStatus = attachment.downloadingStatus,
                onMessageClick = onMessageClick
            )
        }
    }
}

@Composable
private fun DocumentAttachment(
    name: String,
    messageStatus: MessageStatus,
    downloadingStatus: DownloadingStatus,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CustomTheme.colors.chat.input)
            .padding(end = 8.dp)
            .clickable {
                onMessageClick()
            }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_document),
            contentDescription = null,
            tint = CustomTheme.colors.chat.primary,
            modifier = Modifier
        )

        Text(
            text = name,
            style = CustomTheme.typography.body.regular,
            color = CustomTheme.colors.text.secondary,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )

        when {
            messageStatus == MessageStatus.Sending ||
                    downloadingStatus == DownloadingStatus.InProgress -> {
                CircularProgressIndicator(
                    color = CustomTheme.colors.chat.primary,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                        .clickableNoRipple { onMessageClick() }
                        .padding(start = 4.dp)
                )
            }

            downloadingStatus.isNotDownloaded -> {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    tint = CustomTheme.colors.chat.primary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickableNoRipple { onMessageClick() }
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun MediaAttachment(
    name: String,
    localPath: String?,
    messageStatus: MessageStatus,
    downloadingStatus: DownloadingStatus,
    onMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageLoader by remember {
        derivedStateOf {
            ImageLoader.Builder(context)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .build()
        }
    }

    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CustomTheme.colors.chat.input)
                .clickable {
                    onMessageClick()
                },
            model = if (downloadingStatus == DownloadingStatus.Downloaded) {
                localPath
            } else {
                null
            },
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Text(
            text = name,
            style = CustomTheme.typography.caption.regular,
            color = CustomTheme.colors.palette.white,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(180.dp)
                .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp))
                .background(CustomTheme.colors.palette.black50)
                .align(Alignment.BottomStart)
                .padding(horizontal = 8.dp)
        )

        when {
            messageStatus == MessageStatus.Sending ||
                    downloadingStatus == DownloadingStatus.InProgress -> {
                CircularProgressIndicator(
                    color = CustomTheme.colors.chat.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickableNoRipple { onMessageClick() }
                )
            }

            downloadingStatus.isNotDownloaded -> {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = null,
                    tint = CustomTheme.colors.chat.primary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickableNoRipple { onMessageClick() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatMessageItemPreview() {
    AppTheme {
        Column {
            ChatMessageItem(
                message = ChatMessage(
                    id = ChatMessageId.generateLocalId(),
                    author = MessageAuthor.Me,
                    text = "Hello!",
                    time = LocalDateTime.now(),
                    attachment = null,
                    messageStatus = MessageStatus.Sent
                ),
                onMessageClick = {}
            )

            ChatMessageItem(
                message = ChatMessage(
                    id = ChatMessageId.generateLocalId(),
                    author = MessageAuthor.User(StringDesc.Raw("User")),
                    text = "Hi!",
                    time = LocalDateTime.now(),
                    attachment = null,
                    messageStatus = MessageStatus.Sent
                ),
                onMessageClick = {}
            )

            ChatMessageItem(
                message = ChatMessage(
                    id = ChatMessageId.generateLocalId(),
                    author = MessageAuthor.Me,
                    text = "",
                    time = LocalDateTime.now(),
                    attachment = ChatAttachment(
                        localFilePath = "",
                        remoteLink = "",
                        filename = "doc.pdf",
                        extension = ".pdf",
                        type = ChatAttachment.Type.FILE,
                        downloadingStatus = DownloadingStatus.NotDownloaded
                    ),
                    messageStatus = MessageStatus.Sent
                ),
                onMessageClick = {}
            )
        }
    }
}