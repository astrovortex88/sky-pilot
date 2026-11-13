package com.Lutvepo3.Dumnuc.presentation.goals

data class GoalRow(
    val index: Int,
    val titleRes: Int,
    val progress: Int,
    val target: Int,
    val fraction: Float,
    val rewardPoints: Int,
    val rewardCrates: Int,
    val complete: Boolean,
    val claimed: Boolean
)

data class DailyGoalsUiState(
    val rows: List<GoalRow> = emptyList(),
    val resetHours: Int = 0,
    val resetMinutes: Int = 0,
    val allClaimed: Boolean = false
)
