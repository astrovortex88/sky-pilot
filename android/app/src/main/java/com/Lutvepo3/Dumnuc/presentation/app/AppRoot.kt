package com.Lutvepo3.Dumnuc.presentation.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.navigation.Screen
import com.Lutvepo3.Dumnuc.core.ui.ProvideMotionPreference
import com.Lutvepo3.Dumnuc.presentation.collection.CollectionScreen
import com.Lutvepo3.Dumnuc.presentation.game.GameScreen
import com.Lutvepo3.Dumnuc.presentation.gameover.GameOverScreen
import com.Lutvepo3.Dumnuc.presentation.goals.DailyGoalsScreen
import com.Lutvepo3.Dumnuc.presentation.menu.MenuScreen
import com.Lutvepo3.Dumnuc.presentation.onboarding.OnboardingScreen
import com.Lutvepo3.Dumnuc.presentation.profile.ProfileScreen
import com.Lutvepo3.Dumnuc.presentation.progress.ProgressMapScreen
import com.Lutvepo3.Dumnuc.presentation.rewards.RewardsScreen
import com.Lutvepo3.Dumnuc.presentation.settings.SettingsScreen
import com.Lutvepo3.Dumnuc.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val reducedMotion by appViewModel.reducedMotion.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler(enabled = true) {
        if (!appViewModel.navigateBack()) {
            (context as? Activity)?.finish()
        }
    }

    ProvideMotionPreference(reducedMotion = reducedMotion) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                when {
                    initialState is Screen.Splash ->
                        fadeIn(tween(700)) togetherWith fadeOut(tween(500))

                    targetState is Screen.Game ->
                        (slideInVertically(tween(320)) { full -> full / 6 } +
                            fadeIn(tween(320))) togetherWith fadeOut(tween(220))

                    else ->
                        (slideInHorizontally(tween(280)) { full -> full / 5 } +
                            fadeIn(tween(280))) togetherWith fadeOut(tween(220))
                }
            },
            label = "screenHost"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(
                    onFinished = appViewModel::completeSplash
                )

                is Screen.Onboarding -> OnboardingScreen(
                    onDone = appViewModel::goToMenu
                )

                is Screen.Menu -> MenuScreen(
                    onPlay = { stage -> appViewModel.navigateTo(Screen.Game(stage)) },
                    onOpen = appViewModel::navigateTo
                )

                is Screen.Game -> GameScreen(
                    stageIndex = current.stageIndex,
                    onFinished = { resultId ->
                        appViewModel.replaceWith(Screen.GameOver(resultId))
                    },
                    onMenu = appViewModel::goToMenu
                )

                is Screen.GameOver -> GameOverScreen(
                    onPlayAgain = { stage -> appViewModel.replaceWith(Screen.Game(stage)) },
                    onMenu = appViewModel::goToMenu
                )

                is Screen.ProgressMap -> ProgressMapScreen(
                    onFlyStage = { stage -> appViewModel.navigateTo(Screen.Game(stage)) },
                    onMenu = appViewModel::goToMenu
                )

                is Screen.DailyGoals -> DailyGoalsScreen(
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Collection -> CollectionScreen(
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Rewards -> RewardsScreen(
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Profile -> ProfileScreen(
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Settings -> SettingsScreen(
                    onMenu = appViewModel::goToMenu
                )
            }
        }
    }
}
