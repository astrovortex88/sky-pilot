package com.Lutvepo3.Dumnuc.domain.usecase

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.domain.model.GoalKind
import com.Lutvepo3.Dumnuc.domain.model.RunOutcome
import com.Lutvepo3.Dumnuc.domain.model.RunResult
import kotlin.math.roundToInt

class EvaluateGoalsUseCase {

    operator fun invoke(kind: GoalKind, result: RunResult, bankedInARow: Int): Int {
        val banked = result.outcome != RunOutcome.PLANE_LOST
        return when (kind) {
            GoalKind.BANK_RUNS -> if (banked) 1 else 0
            GoalKind.REACH_ALTITUDE -> if (result.multiplier >= 3f) 1 else 0
            GoalKind.COLLECT_RINGS -> result.rings
            GoalKind.FIRE_BOOSTS -> result.boosts
            GoalKind.CLEAR_STAGE -> if (result.stageCleared) 1 else 0
            GoalKind.LAND_SAFE -> if (result.outcome == RunOutcome.SAFE_LANDING) 1 else 0
            GoalKind.REACH_STREAK -> if (GameConfig.streakFactor(bankedInARow) >= 2f) 1 else 0
            GoalKind.FLY_SECONDS -> result.durationSec.roundToInt()
        }
    }
}
