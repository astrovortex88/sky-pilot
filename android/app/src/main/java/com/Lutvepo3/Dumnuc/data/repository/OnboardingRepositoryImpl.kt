package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(
    private val prefs: SkyPilotPreferences
) : OnboardingRepository {

    override fun isSeen(): Boolean =
        prefs.bool(SkyPilotPreferences.KEY_ONBOARDING_SEEN, false)

    override fun markSeen() {
        prefs.putBool(SkyPilotPreferences.KEY_ONBOARDING_SEEN, true)
    }

    override fun resetAll() {
        prefs.putBool(SkyPilotPreferences.KEY_ONBOARDING_SEEN, false)
    }
}
