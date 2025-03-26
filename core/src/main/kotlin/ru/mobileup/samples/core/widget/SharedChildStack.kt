package ru.mobileup.samples.core.widget

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.ChildStack

val LocalSharedScope = staticCompositionLocalOf<SharedScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Immutable
data class SharedScope(
    private val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope,
) {
    companion object {
        @OptIn(ExperimentalSharedTransitionApi::class)
        fun Modifier.sharedScopeModifier(
            block: @Composable SharedTransitionScope.(animScope: AnimatedVisibilityScope) -> Modifier
        ): Modifier = composed {
            return@composed then(
                LocalSharedScope.current?.let { localSharedScope ->
                    block(localSharedScope.sharedTransitionScope, localSharedScope.animatedVisibilityScope)
                } ?: Modifier
            )
        }
    }
}

@OptIn(ExperimentalDecomposeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun <C : Any, T : Any> SharedChildStack(
    stack: ChildStack<C, T>,
    modifier: Modifier = Modifier,
    animation: StackAnimation<C, T> = stackAnimation(),
    content: @Composable AnimatedVisibilityScope.(child: Child.Created<C, T>) -> Unit,
) {
    SharedTransitionScope { sharedTransitionModifier ->
        ChildStack(
            modifier = modifier.then(sharedTransitionModifier),
            stack = stack,
            animation = animation
        ) { child ->
            val sharedScope by remember(this@SharedTransitionScope, this@ChildStack) {
                mutableStateOf(
                    SharedScope(
                        sharedTransitionScope = this@SharedTransitionScope,
                        animatedVisibilityScope = this@ChildStack
                    )
                )
            }
            CompositionLocalProvider(
                LocalSharedScope provides sharedScope
            ) {
                content(child)
            }
        }
    }
}