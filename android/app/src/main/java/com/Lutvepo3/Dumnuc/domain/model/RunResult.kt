package com.Lutvepo3.Dumnuc.domain.model

data class RunResult(
    val id: Long,
    val stageIndex: Int,
    val stageNameRes: Int,
    val outcome: RunOutcome,
    val multiplier: Float,
    val stageTarget: Float,
    val basePoints: Int,
    val streakFactor: Float,
    val assistFactor: Float,
    val pointsEarned: Int,
    val rings: Int,
    val boosts: Int,
    val durationSec: Float,
    val stageCleared: Boolean,
    val stageUnlocked: Int,
    val trackCompleted: Boolean,
    val crateEarned: Boolean,
    val newBest: Boolean,
    val bestMultiplier: Float,
    val seed: Int,
    val unlockedItemNameRes: Int?
)
