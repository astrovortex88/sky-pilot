package com.Lutvepo3.Dumnuc.domain.model

data class Stage(
    val index: Int,
    val nameRes: Int,
    val target: Float
)

data class StageProgress(
    val stage: Stage,
    val cleared: Boolean,
    val current: Boolean,
    val locked: Boolean,
    val bestMultiplier: Float
)
