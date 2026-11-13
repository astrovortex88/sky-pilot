package com.Lutvepo3.Dumnuc.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Lutvepo3.Dumnuc.presentation.app.AppViewModel
import com.Lutvepo3.Dumnuc.presentation.collection.CollectionViewModel
import com.Lutvepo3.Dumnuc.presentation.game.GameViewModel
import com.Lutvepo3.Dumnuc.presentation.gameover.GameOverViewModel
import com.Lutvepo3.Dumnuc.presentation.goals.DailyGoalsViewModel
import com.Lutvepo3.Dumnuc.presentation.menu.MenuViewModel
import com.Lutvepo3.Dumnuc.presentation.onboarding.OnboardingViewModel
import com.Lutvepo3.Dumnuc.presentation.profile.ProfileViewModel
import com.Lutvepo3.Dumnuc.presentation.progress.ProgressMapViewModel
import com.Lutvepo3.Dumnuc.presentation.rewards.RewardsViewModel
import com.Lutvepo3.Dumnuc.presentation.settings.SettingsViewModel
import com.Lutvepo3.Dumnuc.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val deps: AppDependencies
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(deps.onboarding, deps.settings)

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(deps.settings)

            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(deps.onboarding, deps.settings)

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(
                    deps.progress,
                    deps.goals,
                    deps.collection,
                    deps.rewards,
                    deps.profile,
                    deps.settings,
                    deps.resolveRank
                )

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(
                    deps.progress,
                    deps.settings,
                    deps.collection,
                    deps.profile,
                    deps.goals,
                    deps.rewards,
                    deps.startRun,
                    deps.tickRun,
                    deps.bankRun
                )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(deps.progress, deps.settings, deps.bankRun)

            modelClass.isAssignableFrom(ProgressMapViewModel::class.java) ->
                ProgressMapViewModel(deps.progress, deps.settings)

            modelClass.isAssignableFrom(DailyGoalsViewModel::class.java) ->
                DailyGoalsViewModel(deps.goals, deps.progress, deps.rewards)

            modelClass.isAssignableFrom(CollectionViewModel::class.java) ->
                CollectionViewModel(deps.collection)

            modelClass.isAssignableFrom(RewardsViewModel::class.java) ->
                RewardsViewModel(deps.rewards, deps.progress, deps.profile, deps.settings)

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(deps.profile, deps.progress, deps.collection, deps.resolveRank)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(deps.settings, deps)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
