package com.Lutvepo3.Dumnuc.core.di

import android.content.Context
import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.data.repository.CollectionRepositoryImpl
import com.Lutvepo3.Dumnuc.data.repository.GoalsRepositoryImpl
import com.Lutvepo3.Dumnuc.data.repository.OnboardingRepositoryImpl
import com.Lutvepo3.Dumnuc.data.repository.ProfileRepositoryImpl
import com.Lutvepo3.Dumnuc.data.repository.ProgressRepositoryImpl
import com.Lutvepo3.Dumnuc.data.repository.RewardsRepositoryImpl
import com.Lutvepo3.Dumnuc.data.repository.SettingsRepositoryImpl
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository
import com.Lutvepo3.Dumnuc.domain.repository.GoalsRepository
import com.Lutvepo3.Dumnuc.domain.repository.OnboardingRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProfileRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.RewardsRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import com.Lutvepo3.Dumnuc.domain.usecase.BankRunUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.EvaluateGoalsUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.OpenCrateUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.ResolveRankUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.StartRunUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.TickRunUseCase

class AppDependencies(context: Context) {

    private val prefs = SkyPilotPreferences(context)

    val evaluateGoals = EvaluateGoalsUseCase()
    val openCrate = OpenCrateUseCase()
    val startRun = StartRunUseCase()
    val tickRun = TickRunUseCase()
    val bankRun = BankRunUseCase()
    val resolveRank = ResolveRankUseCase()

    val progress: ProgressRepository = ProgressRepositoryImpl(prefs)
    val settings: SettingsRepository = SettingsRepositoryImpl(prefs)
    val onboarding: OnboardingRepository = OnboardingRepositoryImpl(prefs)
    val profile: ProfileRepository = ProfileRepositoryImpl(prefs)
    val collection: CollectionRepository = CollectionRepositoryImpl(prefs)
    val goals: GoalsRepository = GoalsRepositoryImpl(prefs, evaluateGoals)
    val rewards: RewardsRepository = RewardsRepositoryImpl(prefs, openCrate, collection)

    val viewModelFactory = AppViewModelFactory(this)

    init {
        if (!prefs.bool(SkyPilotPreferences.KEY_STORE_READY, false)) {
            prefs.putBool(SkyPilotPreferences.KEY_STORE_READY, true)
            prefs.putInt(SkyPilotPreferences.KEY_POINTS, GameConfig.STARTING_POINTS)
            prefs.putInt(SkyPilotPreferences.KEY_LIFETIME_POINTS, GameConfig.STARTING_POINTS)
            prefs.putInt(SkyPilotPreferences.KEY_CURRENT_STAGE, 1)
            prefs.putIds(SkyPilotPreferences.KEY_UNLOCKED, setOf(SampleData.DEFAULT_LIVERY))
            prefs.putText(SkyPilotPreferences.KEY_EQUIPPED_LIVERY, SampleData.DEFAULT_LIVERY)
            prefs.putText(SkyPilotPreferences.KEY_CALLSIGN, SampleData.DEFAULT_CALLSIGN)
        }
    }

    fun wipeEverything() {
        progress.resetAll()
        profile.resetAll()
        collection.resetAll()
        rewards.resetAll()
        goals.resetAll()
    }
}

object ServiceLocator {

    private var dependencies: AppDependencies? = null

    fun init(context: Context) {
        if (dependencies == null) {
            dependencies = AppDependencies(context.applicationContext)
        }
    }

    fun deps(): AppDependencies = checkNotNull(dependencies)

    fun factory() = deps().viewModelFactory
}
