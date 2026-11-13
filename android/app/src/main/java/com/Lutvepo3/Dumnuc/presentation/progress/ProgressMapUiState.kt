package com.Lutvepo3.Dumnuc.presentation.progress

data class StageRow(
    val index: Int,
    val nameRes: Int,
    val targetLabel: String,
    val bestLabel: String,
    val hasBest: Boolean,
    val cleared: Boolean,
    val current: Boolean,
    val locked: Boolean
)

data class ProgressMapUiState(
    val rows: List<StageRow> = emptyList(),
    val cleared: Int = 0,
    val total: Int = 12,
    val fraction: Float = 0f,
    val trackCompleted: Boolean = false,
    val reducedMotion: Boolean = false
)
