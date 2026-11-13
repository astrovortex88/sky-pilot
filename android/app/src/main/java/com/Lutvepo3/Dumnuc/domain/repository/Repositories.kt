package com.Lutvepo3.Dumnuc.domain.repository

import com.Lutvepo3.Dumnuc.domain.model.CollectionEntry
import com.Lutvepo3.Dumnuc.domain.model.CrateOpenResult
import com.Lutvepo3.Dumnuc.domain.model.CrateTier
import com.Lutvepo3.Dumnuc.domain.model.CrateStock
import com.Lutvepo3.Dumnuc.domain.model.DailyGoal
import com.Lutvepo3.Dumnuc.domain.model.GameSettings
import com.Lutvepo3.Dumnuc.domain.model.PilotProfile
import com.Lutvepo3.Dumnuc.domain.model.RunResult
import com.Lutvepo3.Dumnuc.domain.model.StageProgress

interface ProgressRepository {
    fun currentStage(): Int
    fun stages(): List<StageProgress>
    fun clearedCount(): Int
    fun trackCompleted(): Boolean
    fun bestMultiplier(): Float
    fun bankedInARow(): Int
    fun points(): Int
    fun runIndex(): Int
    fun lastResult(): RunResult?
    fun storeResult(result: RunResult)
    fun applyRun(result: RunResult)
    fun selectStage(stageIndex: Int)
    fun addPoints(amount: Int)
    fun resetAll()
}

interface GoalsRepository {
    fun goals(): List<DailyGoal>
    fun resetsInMinutes(): Int
    fun recordRun(result: RunResult, bankedInARow: Int)
    fun claim(index: Int): DailyGoal?
    fun resetAll()
}

interface CollectionRepository {
    fun entries(): List<CollectionEntry>
    fun unlockedCount(): Int
    fun equippedLiveryColor(): Long
    fun equippedBadgeNameRes(): Int?
    fun equip(itemId: String)
    fun refreshUnlocks(profile: PilotProfile, clearedStages: Int, trackCompleted: Boolean): Int?
    fun unlockRandomLocked(roll: Float): Int?
    fun resetAll()
}

interface RewardsRepository {
    fun stock(): CrateStock
    fun grantCrates(standard: Int, gold: Int)
    fun open(tier: CrateTier, roll: Float, itemRoll: Float): CrateOpenResult
    fun resetAll()
}

interface ProfileRepository {
    fun profile(): PilotProfile
    fun callsignId(): String
    fun setCallsign(id: String)
    fun recordRun(result: RunResult, bankedInARow: Int)
    fun recordCrateOpened()
    fun resetAll()
}

interface SettingsRepository {
    fun settings(): GameSettings
    fun setControlSideLeft(value: Boolean)
    fun setAssistMode(value: Boolean)
    fun setAutoCashOut(value: Float)
    fun setReducedMotion(value: Boolean)
}

interface OnboardingRepository {
    fun isSeen(): Boolean
    fun markSeen()
    fun resetAll()
}
