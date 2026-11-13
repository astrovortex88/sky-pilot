package com.Lutvepo3.Dumnuc.domain.model

enum class CrateTier {
    STANDARD,
    GOLD
}

data class CrateStock(
    val standard: Int,
    val gold: Int,
    val openedToday: Int,
    val pointsToday: Int
) {
    val total: Int get() = standard + gold
}

data class CrateOpenResult(
    val tier: CrateTier,
    val points: Int,
    val itemNameRes: Int?
)
