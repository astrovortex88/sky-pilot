package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.model.PilotProfile
import com.Lutvepo3.Dumnuc.domain.model.RunOutcome
import com.Lutvepo3.Dumnuc.domain.model.RunResult
import com.Lutvepo3.Dumnuc.domain.repository.ProfileRepository
import kotlin.math.roundToInt

class ProfileRepositoryImpl(
    private val prefs: SkyPilotPreferences
) : ProfileRepository {

    override fun profile(): PilotProfile = PilotProfile(
        callsignId = callsignId(),
        totalPoints = prefs.int(SkyPilotPreferences.KEY_POINTS, 0),
        lifetimePoints = prefs.int(SkyPilotPreferences.KEY_LIFETIME_POINTS, 0),
        runsFlown = prefs.int(SkyPilotPreferences.KEY_RUNS_FLOWN, 0),
        bestMultiplier = prefs.float(SkyPilotPreferences.KEY_BEST_MULTIPLIER, 0f),
        ringsCollected = prefs.int(SkyPilotPreferences.KEY_RINGS, 0),
        stagesCleared = clearedCount(),
        bestStreak = prefs.int(SkyPilotPreferences.KEY_BEST_STREAK, 0),
        boostsFired = prefs.int(SkyPilotPreferences.KEY_BOOSTS, 0),
        bankedRuns = prefs.int(SkyPilotPreferences.KEY_BANKED_RUNS, 0),
        cratesOpened = prefs.int(SkyPilotPreferences.KEY_CRATES_OPENED, 0),
        secondsFlown = prefs.int(SkyPilotPreferences.KEY_SECONDS, 0)
    )

    override fun callsignId(): String =
        prefs.text(SkyPilotPreferences.KEY_CALLSIGN, SampleData.DEFAULT_CALLSIGN)

    override fun setCallsign(id: String) {
        prefs.putText(SkyPilotPreferences.KEY_CALLSIGN, id)
    }

    override fun recordRun(result: RunResult, bankedInARow: Int) {
        bump(SkyPilotPreferences.KEY_RUNS_FLOWN, 1)
        bump(SkyPilotPreferences.KEY_RINGS, result.rings)
        bump(SkyPilotPreferences.KEY_BOOSTS, result.boosts)
        bump(SkyPilotPreferences.KEY_SECONDS, result.durationSec.roundToInt())
        if (result.outcome != RunOutcome.PLANE_LOST) {
            bump(SkyPilotPreferences.KEY_BANKED_RUNS, 1)
        }
    }

    override fun recordCrateOpened() {
        bump(SkyPilotPreferences.KEY_CRATES_OPENED, 1)
    }

    override fun resetAll() {
        prefs.putInt(SkyPilotPreferences.KEY_RUNS_FLOWN, 0)
        prefs.putInt(SkyPilotPreferences.KEY_RINGS, 0)
        prefs.putInt(SkyPilotPreferences.KEY_BOOSTS, 0)
        prefs.putInt(SkyPilotPreferences.KEY_SECONDS, 0)
        prefs.putInt(SkyPilotPreferences.KEY_BANKED_RUNS, 0)
        prefs.putInt(SkyPilotPreferences.KEY_CRATES_OPENED, 0)
        prefs.putText(SkyPilotPreferences.KEY_CALLSIGN, SampleData.DEFAULT_CALLSIGN)
    }

    private fun bump(key: String, amount: Int) {
        if (amount <= 0) return
        prefs.putInt(key, prefs.int(key, 0) + amount)
    }

    private fun clearedCount(): Int {
        val mask = prefs.int(SkyPilotPreferences.KEY_CLEARED_MASK, 0)
        var count = 0
        for (index in 0 until SampleData.stages.size) {
            if (mask and (1 shl index) != 0) count++
        }
        return count
    }
}
