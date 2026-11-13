package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.domain.model.GameSettings
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val prefs: SkyPilotPreferences
) : SettingsRepository {

    override fun settings(): GameSettings = GameSettings(
        controlSideLeft = prefs.bool(SkyPilotPreferences.KEY_CONTROL_SIDE_LEFT, false),
        assistMode = prefs.bool(SkyPilotPreferences.KEY_ASSIST_MODE, true),
        autoCashOut = prefs.float(SkyPilotPreferences.KEY_AUTO_CASH_OUT, 0f),
        reducedMotion = prefs.bool(SkyPilotPreferences.KEY_REDUCED_MOTION, false)
    )

    override fun setControlSideLeft(value: Boolean) {
        prefs.putBool(SkyPilotPreferences.KEY_CONTROL_SIDE_LEFT, value)
    }

    override fun setAssistMode(value: Boolean) {
        prefs.putBool(SkyPilotPreferences.KEY_ASSIST_MODE, value)
    }

    override fun setAutoCashOut(value: Float) {
        prefs.putFloat(SkyPilotPreferences.KEY_AUTO_CASH_OUT, value)
    }

    override fun setReducedMotion(value: Boolean) {
        prefs.putBool(SkyPilotPreferences.KEY_REDUCED_MOTION, value)
    }
}
