package com.Lutvepo3.Dumnuc.core.config

object GameConfig {

    const val LOADER_DURATION_MS = 8000L

    const val TICK_MS = 16L
    const val SKY_HEIGHT_DP = 600f

    const val RUN_FUEL_SECONDS = 28f
    const val RUN_MAX_SECONDS = 45f
    const val RUN_BASE_POINTS = 50
    const val START_MULTIPLIER = 1.00f
    const val CLIMB_EARLY_PER_SEC = 0.10f
    const val CLIMB_LATE_PER_SEC = 0.18f
    const val CLIMB_SWITCH_SEC = 10f

    const val RING_MULTIPLIER_BONUS = 0.25f
    const val RING_FUEL_BONUS_SEC = 4f
    const val BOOST_CHAIN_LENGTH = 3
    const val BOOST_MULTIPLIER_BONUS = 0.50f
    const val BOOST_INVULN_SEC = 1.5f

    const val ASSIST_WARNING_SEC = 0.6f
    const val ASSIST_RECOVERY_PENALTY = 0.40f
    const val ASSIST_RECOVERY_SEC = 0.8f
    const val NO_ASSIST_POINT_FACTOR = 1.25f

    const val LANE_COUNT = 3
    const val LANE_SHIFT_SEC = 0.18f
    const val PLANE_ROW_FRACTION = 0.72f
    const val CASH_OUT_LOCK_SEC = 0.8f
    const val CONTROL_HINT_SEC = 6f

    const val FIRST_HAZARD_SEC = 2.6f
    const val RING_INTERVAL_SEC = 2.10f
    const val FIRST_RING_SEC = 3.4f
    const val SPAWN_HORIZON_SEC = 52f

    const val STAGE_COUNT = 12
    const val STARTING_POINTS = 500

    const val CRATE_POINTS = 150
    const val CRATE_ITEM_CHANCE = 0.35f

    const val DAILY_GOAL_SLOTS = 3

    private val STREAK_FACTORS = floatArrayOf(1.0f, 1.5f, 2.0f)

    fun worldSpeedDpPerSec(stageIndex: Int): Float = 320f + 14f * (stageIndex - 1)

    fun worldSpeedUnitsPerSec(stageIndex: Int): Float =
        worldSpeedDpPerSec(stageIndex) / SKY_HEIGHT_DP

    fun hazardIntervalSec(stageIndex: Int): Float =
        (1.60f - 0.06f * (stageIndex - 1)).coerceAtLeast(0.92f)

    fun stageTarget(stageIndex: Int): Float = 1.50f + 0.35f * stageIndex

    fun streakFactor(bankedInARow: Int): Float =
        STREAK_FACTORS[bankedInARow.coerceIn(0, STREAK_FACTORS.size - 1)]

    fun maxStreakIndex(): Int = STREAK_FACTORS.size - 1

    fun fuelCeiling(elapsedSec: Float): Float =
        minOf(RUN_FUEL_SECONDS, (RUN_MAX_SECONDS - elapsedSec).coerceAtLeast(0f))
}
