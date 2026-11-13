package com.Lutvepo3.Dumnuc.presentation.rewards

data class CrateReveal(
    val points: Int,
    val itemNameRes: Int?
)

data class RewardsUiState(
    val standard: Int = 0,
    val gold: Int = 0,
    val total: Int = 0,
    val openedToday: Int = 0,
    val pointsToday: String = "",
    val showToday: Boolean = false,
    val reveal: CrateReveal? = null,
    val reducedMotion: Boolean = false
)
