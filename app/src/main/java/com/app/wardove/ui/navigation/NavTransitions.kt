package com.app.wardove.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

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

val enterFade: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    fadeIn(tween(ANIM_DURATION))
}

val exitFade: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    fadeOut(tween(ANIM_DURATION))
}

val popEnterFade = enterFade
val popExitFade = exitFade
