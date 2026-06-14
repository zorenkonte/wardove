package com.app.wardove.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIntoContainer
import androidx.compose.animation.slideOutOfContainer
import androidx.compose.animation.AnimatedContentTransitionScope

private const val ANIM_DURATION = 300

val enterSlide: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(ANIM_DURATION)
    ) + fadeIn(tween(ANIM_DURATION))
}

val exitSlide: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(ANIM_DURATION)
    ) + fadeOut(tween(ANIM_DURATION))
}

val popEnterSlide: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(ANIM_DURATION)
    ) + fadeIn(tween(ANIM_DURATION))
}

val popExitSlide: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(ANIM_DURATION)
    ) + fadeOut(tween(ANIM_DURATION))
}
