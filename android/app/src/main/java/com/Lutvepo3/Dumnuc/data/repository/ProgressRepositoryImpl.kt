package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.model.RunOutcome
import com.Lutvepo3.Dumnuc.domain.model.RunResult
import com.Lutvepo3.Dumnuc.domain.model.StageProgress
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository

class ProgressRepositoryImpl(
    private val prefs: SkyPilotPreferences
) : ProgressRepository {

    private var cachedResult: RunResult? = null

    override fun currentStage(): Int =
        prefs.int(SkyPilotPreferences.KEY_CURRENT_STAGE, 1)
            .coerceIn(1, GameConfig.STAGE_COUNT)

    override fun stages(): List<StageProgress> {
        val mask = prefs.int(SkyPilotPreferences.KEY_CLEARED_MASK, 0)
        val unlockedThrough = highestUnlocked(mask)
        val current = currentStage()
        return SampleData.stages.map { stage ->
            StageProgress(
                stage = stage,
                cleared = isCleared(mask, stage.index),
                current = stage.index == current,
                locked = stage.index > unlockedThrough,
                bestMultiplier = prefs.float(
                    SkyPilotPreferences.KEY_STAGE_BEST + stage.index,
                    0f
                )
            )
        }
    }

    override fun clearedCount(): Int {
        val mask = prefs.int(SkyPilotPreferences.KEY_CLEARED_MASK, 0)
        var count = 0
        for (index in 1..GameConfig.STAGE_COUNT) {
            if (isCleared(mask, index)) count++
        }
        return count
    }

    override fun trackCompleted(): Boolean = clearedCount() >= GameConfig.STAGE_COUNT

    override fun bestMultiplier(): Float =
        prefs.float(SkyPilotPreferences.KEY_BEST_MULTIPLIER, 0f)

    override fun bankedInARow(): Int =
        prefs.int(SkyPilotPreferences.KEY_BANKED_IN_A_ROW, 0)

    override fun points(): Int = prefs.int(SkyPilotPreferences.KEY_POINTS, 0)

    override fun runIndex(): Int = prefs.int(SkyPilotPreferences.KEY_RUN_INDEX, 0)

    override fun lastResult(): RunResult? = cachedResult

    override fun storeResult(result: RunResult) {
        cachedResult = result
    }

    override fun applyRun(result: RunResult) {
        prefs.putInt(SkyPilotPreferences.KEY_RUN_INDEX, runIndex() + 1)

        val banked = result.outcome != RunOutcome.PLANE_LOST
        val streak = if (banked) {
            (bankedInARow() + 1).coerceAtMost(GameConfig.maxStreakIndex())
        } else {
            0
        }
        prefs.putInt(SkyPilotPreferences.KEY_BANKED_IN_A_ROW, streak)
        val bestStreak = prefs.int(SkyPilotPreferences.KEY_BEST_STREAK, 0)
        if (streak > bestStreak) prefs.putInt(SkyPilotPreferences.KEY_BEST_STREAK, streak)

        if (result.multiplier > bestMultiplier()) {
            prefs.putFloat(SkyPilotPreferences.KEY_BEST_MULTIPLIER, result.multiplier)
        }
        val stageBestKey = SkyPilotPreferences.KEY_STAGE_BEST + result.stageIndex
        if (result.multiplier > prefs.float(stageBestKey, 0f)) {
            prefs.putFloat(stageBestKey, result.multiplier)
        }

        if (result.pointsEarned > 0) addPoints(result.pointsEarned)

        if (result.stageCleared) {
            val mask = prefs.int(SkyPilotPreferences.KEY_CLEARED_MASK, 0)
            prefs.putInt(SkyPilotPreferences.KEY_CLEARED_MASK, mask or bitFor(result.stageIndex))
            val next = (result.stageIndex + 1).coerceAtMost(GameConfig.STAGE_COUNT)
            prefs.putInt(SkyPilotPreferences.KEY_CURRENT_STAGE, next)
        }
    }

    override fun selectStage(stageIndex: Int) {
        prefs.putInt(
            SkyPilotPreferences.KEY_CURRENT_STAGE,
            stageIndex.coerceIn(1, GameConfig.STAGE_COUNT)
        )
    }

    override fun addPoints(amount: Int) {
        if (amount <= 0) return
        prefs.putInt(SkyPilotPreferences.KEY_POINTS, points() + amount)
        prefs.putInt(
            SkyPilotPreferences.KEY_LIFETIME_POINTS,
            prefs.int(SkyPilotPreferences.KEY_LIFETIME_POINTS, 0) + amount
        )
    }

    override fun resetAll() {
        cachedResult = null
        prefs.putInt(SkyPilotPreferences.KEY_POINTS, GameConfig.STARTING_POINTS)
        prefs.putInt(SkyPilotPreferences.KEY_LIFETIME_POINTS, GameConfig.STARTING_POINTS)
        prefs.putInt(SkyPilotPreferences.KEY_CURRENT_STAGE, 1)
        prefs.putInt(SkyPilotPreferences.KEY_CLEARED_MASK, 0)
        prefs.putInt(SkyPilotPreferences.KEY_BANKED_IN_A_ROW, 0)
        prefs.putInt(SkyPilotPreferences.KEY_BEST_STREAK, 0)
        prefs.putInt(SkyPilotPreferences.KEY_RUN_INDEX, 0)
        prefs.putFloat(SkyPilotPreferences.KEY_BEST_MULTIPLIER, 0f)
        for (index in 1..GameConfig.STAGE_COUNT) {
            prefs.putFloat(SkyPilotPreferences.KEY_STAGE_BEST + index, 0f)
        }
    }

    private fun bitFor(stageIndex: Int): Int = 1 shl (stageIndex - 1)

    private fun isCleared(mask: Int, stageIndex: Int): Boolean =
        mask and bitFor(stageIndex) != 0

    private fun highestUnlocked(mask: Int): Int {
        var highest = 0
        for (index in 1..GameConfig.STAGE_COUNT) {
            if (isCleared(mask, index)) highest = index
        }
        return (highest + 1).coerceAtMost(GameConfig.STAGE_COUNT)
    }
}
