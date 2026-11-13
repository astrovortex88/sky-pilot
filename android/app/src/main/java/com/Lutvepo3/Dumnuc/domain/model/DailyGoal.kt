package com.Lutvepo3.Dumnuc.domain.model

enum class GoalKind {
    BANK_RUNS,
    REACH_ALTITUDE,
    COLLECT_RINGS,
    FIRE_BOOSTS,
    CLEAR_STAGE,
    LAND_SAFE,
    REACH_STREAK,
    FLY_SECONDS
}

data class GoalTemplate(
    val kind: GoalKind,
    val titleRes: Int,
    val target: Int,
    val rewardPoints: Int,
    val rewardCrates: Int
)

data class DailyGoal(
    val kind: GoalKind,
    val titleRes: Int,
    val target: Int,
    val progress: Int,
    val rewardPoints: Int,
    val rewardCrates: Int,
    val claimed: Boolean
) {
    val complete: Boolean get() = progress >= target
    val fraction: Float get() = if (target <= 0) 0f else (progress.toFloat() / target).coerceIn(0f, 1f)
}
