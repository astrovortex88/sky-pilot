package com.Lutvepo3.Dumnuc.domain.model

enum class SpawnKind {
    HAZARD,
    RING
}

data class Spawn(
    val arrivalSec: Float,
    val lane: Int,
    val kind: SpawnKind
)

data class SpawnPlan(
    val seed: Int,
    val spawns: List<Spawn>,
    val fallbackUsed: Boolean
)
