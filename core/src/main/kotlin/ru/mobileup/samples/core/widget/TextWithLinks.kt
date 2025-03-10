package ru.mobileup.samples.core.widget

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import ru.mobileup.samples.core.theme.custom.CustomTheme

private const val START_ANNOTATION_SYMBOL = "["
private const val END_ANNOTATION_SYMBOL = "]"

private sealed interface LinkTextPart {
    data class NormalText(val text: String) : LinkTextPart
    data class LinkText(val text: String, val tag: String) : LinkTextPart
}

@Composable
fun TextWithLinks(
    text: String,
    annotationsTags: List<String>,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    mainSpanStyle: TextStyle = CustomTheme.typography.body.regular.copy(
        color = CustomTheme.colors.text.secondary,
        textDecoration = TextDecoration.Underline
    ),
    annotationSpanStyle: TextStyle = CustomTheme.typography.body.regular.copy(
        color = CustomTheme.colors.text.secondary,
    ),
    textAlign: TextAlign? = null
) {
    val annotatedString = remember(
        text,
        annotationSpanStyle,
        mainSpanStyle,
        annotationsTags,
        textAlign
    ) {
        buildLinkText(
            text,
            annotationsTags,
            annotationSpanStyle,
            mainSpanStyle,
            onLinkClick
        )
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        textAlign = textAlign
    )
}

private fun buildLinkText(
    text: String,
    annotationsTags: List<String>,
    normalTextStyle: TextStyle,
    linkTextStyle: TextStyle,
    onLinkClick: (String) -> Unit
): AnnotatedString = buildAnnotatedString {
    parseStringToLinkTextParts(
        input = text,
        annotationsTags = annotationsTags
    ).forEach { part ->
        when (part) {
            is LinkTextPart.LinkText -> {
                withLink(
                    LinkAnnotation.Clickable(
                        tag = part.tag,
                        styles = TextLinkStyles(linkTextStyle.toSpanStyle()),
                    ) { link ->
                        onLinkClick((link as LinkAnnotation.Clickable).tag)
                    }
                ) {
                    append(part.text)
                }
            }
            is LinkTextPart.NormalText -> {
                withStyle(normalTextStyle.toSpanStyle()) {
                    append(part.text)
                }
            }
        }
    }
}

private fun parseStringToLinkTextParts(
    input: String,
    annotationsTags: List<String>
): List<LinkTextPart> {
    val regex = "\\$START_ANNOTATION_SYMBOL(.*?)$END_ANNOTATION_SYMBOL".toRegex()
    val result = mutableListOf<LinkTextPart>()
    var lastIndex = 0
    regex.findAll(input).forEach { matchResult ->
        val matchStart = matchResult.range.first
        val matchEnd = matchResult.range.last
        if (lastIndex < matchStart) {
            result.add(LinkTextPart.NormalText(input.substring(lastIndex, matchStart)))
        }
        result.add(
            LinkTextPart.LinkText(
                text = matchResult.groups[1]?.value.orEmpty(),
                tag = annotationsTags[result.filterIsInstance<LinkTextPart.LinkText>().count()]
            )
        )
        lastIndex = matchEnd + 1
    }
    if (lastIndex < input.length) {
        result.add(LinkTextPart.NormalText(input.substring(lastIndex)))
    }
    return result
}