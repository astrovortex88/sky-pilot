package com.Lutvepo3.Dumnuc.domain.model

data class ActiveSpawn(
    val id: Int,
    val arrivalSec: Float,
    val lane: Int,
    val kind: SpawnKind,
    val resolved: Boolean = false
)

data class RunState(
    val stageIndex: Int,
    val seed: Int,
    val assistMode: Boolean,
    val autoCashOut: Float,
    val elapsedSec: Float = 0f,
    val multiplier: Float = 1f,
    val fuelSec: Float = 0f,
    val lane: Int = 1,
    val lanePos: Float = 1f,
    val rings: Int = 0,
    val boosts: Int = 0,
    val chain: Int = 0,
    val invulnUntilSec: Float = -1f,
    val recoveringUntilSec: Float = -1f,
    val recoveryUsed: Boolean = false,
    val spawns: List<ActiveSpawn> = emptyList(),
    val finished: Boolean = false,
    val outcome: RunOutcome? = null
) {
    val boosting: Boolean get() = elapsedSec < invulnUntilSec
    val recovering: Boolean get() = elapsedSec < recoveringUntilSec
}

enum class RunEvent {
    RING_COLLECTED,
    BOOST_FIRED,
    RECOVERED,
    PLANE_HIT,
    FUEL_EMPTY,
    AUTO_BANK
}

data class TickResult(
    val state: RunState,
    val events: List<RunEvent>
)
