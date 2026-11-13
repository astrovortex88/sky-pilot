package com.Lutvepo3.Dumnuc.domain.model

data class GameSettings(
    val controlSideLeft: Boolean = false,
    val assistMode: Boolean = true,
    val autoCashOut: Float = 0f,
    val reducedMotion: Boolean = false
)
