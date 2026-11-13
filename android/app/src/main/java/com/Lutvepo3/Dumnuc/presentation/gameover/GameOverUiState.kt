package com.Lutvepo3.Dumnuc.presentation.gameover

import com.Lutvepo3.Dumnuc.R

data class GameOverUiState(
    val ready: Boolean = false,
    val stageIndex: Int = 1,
    val stageNameRes: Int = R.string.stage_1,
    val bannerRes: Int = R.string.outcome_banked,
    val accent: Long = 0xFFF4D06F,
    val lost: Boolean = false,
    val multiplierLabel: String = "",
    val pointsLabel: String = "",
    val points: Int = 0,
    val rings: Int = 0,
    val boosts: Int = 0,
    val streakLabel: String = "",
    val assistLabel: String = "",
    val assistApplied: Boolean = false,
    val basePoints: Int = 50,
    val bestLabel: String = "",
    val newBest: Boolean = false,
    val stageCleared: Boolean = false,
    val stageUnlocked: Int = 2,
    val stageTargetLabel: String = "",
    val crateEarned: Boolean = false,
    val trackCompleted: Boolean = false,
    val unlockedItemNameRes: Int? = null,
    val reducedMotion: Boolean = false
)
