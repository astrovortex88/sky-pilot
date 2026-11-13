package com.Lutvepo3.Dumnuc.domain.usecase

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.domain.model.CrateTier

class OpenCrateUseCase {

    fun points(tier: CrateTier, roll: Float): Int {
        val base = if (tier == CrateTier.GOLD) {
            GameConfig.CRATE_POINTS * 2
        } else {
            GameConfig.CRATE_POINTS
        }
        return base + (roll * 40f).toInt()
    }

    fun rollsItem(tier: CrateTier, itemRoll: Float): Boolean {
        val chance = if (tier == CrateTier.GOLD) {
            GameConfig.CRATE_ITEM_CHANCE * 2f
        } else {
            GameConfig.CRATE_ITEM_CHANCE
        }
        return itemRoll < chance
    }
}
