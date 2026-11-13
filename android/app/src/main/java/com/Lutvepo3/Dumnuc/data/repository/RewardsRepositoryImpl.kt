package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.domain.model.CrateOpenResult
import com.Lutvepo3.Dumnuc.domain.model.CrateStock
import com.Lutvepo3.Dumnuc.domain.model.CrateTier
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository
import com.Lutvepo3.Dumnuc.domain.repository.RewardsRepository
import com.Lutvepo3.Dumnuc.domain.usecase.OpenCrateUseCase
import java.time.LocalDate

class RewardsRepositoryImpl(
    private val prefs: SkyPilotPreferences,
    private val openCrate: OpenCrateUseCase,
    private val collection: CollectionRepository
) : RewardsRepository {

    override fun stock(): CrateStock {
        syncDay()
        return CrateStock(
            standard = prefs.int(SkyPilotPreferences.KEY_CRATES_STANDARD, 0),
            gold = prefs.int(SkyPilotPreferences.KEY_CRATES_GOLD, 0),
            openedToday = prefs.int(SkyPilotPreferences.KEY_CRATES_TODAY, 0),
            pointsToday = prefs.int(SkyPilotPreferences.KEY_CRATE_POINTS_TODAY, 0)
        )
    }

    override fun grantCrates(standard: Int, gold: Int) {
        if (standard > 0) {
            prefs.putInt(
                SkyPilotPreferences.KEY_CRATES_STANDARD,
                prefs.int(SkyPilotPreferences.KEY_CRATES_STANDARD, 0) + standard
            )
        }
        if (gold > 0) {
            prefs.putInt(
                SkyPilotPreferences.KEY_CRATES_GOLD,
                prefs.int(SkyPilotPreferences.KEY_CRATES_GOLD, 0) + gold
            )
        }
    }

    override fun open(tier: CrateTier, roll: Float, itemRoll: Float): CrateOpenResult {
        syncDay()
        val key = if (tier == CrateTier.GOLD) {
            SkyPilotPreferences.KEY_CRATES_GOLD
        } else {
            SkyPilotPreferences.KEY_CRATES_STANDARD
        }
        val available = prefs.int(key, 0)
        if (available <= 0) return CrateOpenResult(tier, 0, null)
        prefs.putInt(key, available - 1)

        var points = openCrate.points(tier, roll)
        var itemNameRes: Int? = null
        if (openCrate.rollsItem(tier, itemRoll)) {
            itemNameRes = collection.unlockRandomLocked(roll)
        }
        if (itemNameRes == null) points += EXTRA_POINTS

        prefs.putInt(
            SkyPilotPreferences.KEY_CRATES_TODAY,
            prefs.int(SkyPilotPreferences.KEY_CRATES_TODAY, 0) + 1
        )
        prefs.putInt(
            SkyPilotPreferences.KEY_CRATE_POINTS_TODAY,
            prefs.int(SkyPilotPreferences.KEY_CRATE_POINTS_TODAY, 0) + points
        )
        return CrateOpenResult(tier, points, itemNameRes)
    }

    override fun resetAll() {
        prefs.putInt(SkyPilotPreferences.KEY_CRATES_STANDARD, 0)
        prefs.putInt(SkyPilotPreferences.KEY_CRATES_GOLD, 0)
        prefs.putInt(SkyPilotPreferences.KEY_CRATES_TODAY, 0)
        prefs.putInt(SkyPilotPreferences.KEY_CRATE_POINTS_TODAY, 0)
    }

    private fun syncDay() {
        val now = LocalDate.now().toEpochDay()
        if (prefs.long(SkyPilotPreferences.KEY_CRATE_DAY, -1L) == now) return
        prefs.putLong(SkyPilotPreferences.KEY_CRATE_DAY, now)
        prefs.putInt(SkyPilotPreferences.KEY_CRATES_TODAY, 0)
        prefs.putInt(SkyPilotPreferences.KEY_CRATE_POINTS_TODAY, 0)
    }

    companion object {
        private const val EXTRA_POINTS = 150
    }
}
