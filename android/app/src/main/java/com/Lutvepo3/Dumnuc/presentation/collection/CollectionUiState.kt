package com.Lutvepo3.Dumnuc.presentation.collection

data class CollectionCell(
    val id: String,
    val nameRes: Int,
    val colorArgb: Long,
    val livery: Boolean,
    val unlocked: Boolean,
    val equipped: Boolean,
    val unlockTextRes: Int,
    val unlockValue: Int
)

data class CollectionUiState(
    val liveries: List<CollectionCell> = emptyList(),
    val badges: List<CollectionCell> = emptyList(),
    val unlocked: Int = 0,
    val total: Int = 12
)
