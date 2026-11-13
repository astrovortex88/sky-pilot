package com.Lutvepo3.Dumnuc.domain.model

enum class CollectibleKind {
    LIVERY,
    BADGE
}

enum class UnlockKind {
    START,
    CLEAR_STAGE,
    OPEN_CRATES,
    REACH_MULTIPLIER,
    BANK_RUNS,
    COLLECT_RINGS,
    FIRE_BOOSTS,
    COMPLETE_TRACK
}

data class CollectibleItem(
    val id: String,
    val nameRes: Int,
    val kind: CollectibleKind,
    val colorArgb: Long,
    val unlockKind: UnlockKind,
    val unlockValue: Int,
    val unlockTextRes: Int
)

data class CollectionEntry(
    val item: CollectibleItem,
    val unlocked: Boolean,
    val equipped: Boolean
)
