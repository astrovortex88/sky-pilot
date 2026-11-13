package com.Lutvepo3.Dumnuc.presentation.game

import com.Lutvepo3.Dumnuc.R

enum class GamePhase {
    IDLE,
    FLYING,
    PAUSED,
    FINISHED
}

data class SkyEntity(
    val id: Int,
    val lane: Int,
    val y: Float,
    val ring: Boolean,
    val warning: Boolean
)

data class GameUiState(
    val phase: GamePhase = GamePhase.IDLE,
    val stageIndex: Int = 1,
    val stageNameRes: Int = R.string.stage_1,
    val stageTarget: String = "",
    val multiplier: Float = 1f,
    val multiplierLabel: String = "",
    val fuelSeconds: Int = 45,
    val fuelFraction: Float = 1f,
    val lanePos: Float = 1f,
    val bankDegrees: Float = 0f,
    val entities: List<SkyEntity> = emptyList(),
    val rings: Int = 0,
    val boosts: Int = 0,
    val streakFactor: Float = 1f,
    val elapsedSeconds: Int = 0,
    val elapsedRaw: Float = 0f,
    val cashOutEnabled: Boolean = false,
    val hintVisible: Boolean = true,
    val boosting: Boolean = false,
    val recovering: Boolean = false,
    val goldFlash: Float = 0f,
    val dangerFlash: Float = 0f,
    val ringPulse: Float = 0f,
    val controlSideLeft: Boolean = false,
    val reducedMotion: Boolean = false,
    val liveryColor: Long = 0xFFB9C6DC,
    val paused: Boolean = false
)
