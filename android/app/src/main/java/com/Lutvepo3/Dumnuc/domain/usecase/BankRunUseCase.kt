package com.Lutvepo3.Dumnuc.domain.usecase

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import kotlin.math.roundToInt

class BankRunUseCase {

    fun assistFactor(assistMode: Boolean): Float =
        if (assistMode) 1f else GameConfig.NO_ASSIST_POINT_FACTOR

    operator fun invoke(multiplier: Float, bankedInARow: Int, assistMode: Boolean): Int {
        val streak = GameConfig.streakFactor(bankedInARow)
        val assist = assistFactor(assistMode)
        return (GameConfig.RUN_BASE_POINTS * multiplier * streak * assist).roundToInt()
    }
}
