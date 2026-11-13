package com.Lutvepo3.Dumnuc.presentation.settings

data class SettingsUiState(
    val controlSideLeft: Boolean = false,
    val assistMode: Boolean = true,
    val autoCashOut: Float = 0f,
    val reducedMotion: Boolean = false,
    val showResetDialog: Boolean = false
)
