package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.model.CollectibleItem
import com.Lutvepo3.Dumnuc.domain.model.CollectibleKind
import com.Lutvepo3.Dumnuc.domain.model.CollectionEntry
import com.Lutvepo3.Dumnuc.domain.model.PilotProfile
import com.Lutvepo3.Dumnuc.domain.model.UnlockKind
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository

class CollectionRepositoryImpl(
    private val prefs: SkyPilotPreferences
) : CollectionRepository {

    override fun entries(): List<CollectionEntry> {
        val unlocked = unlockedIds()
        val livery = equippedLiveryId()
        val badge = equippedBadgeId()
        return SampleData.collectibles.map { item ->
            CollectionEntry(
                item = item,
                unlocked = unlocked.contains(item.id),
                equipped = item.id == livery || item.id == badge
            )
        }
    }

    override fun unlockedCount(): Int = unlockedIds().size

    override fun equippedLiveryColor(): Long =
        SampleData.item(equippedLiveryId())?.colorArgb ?: 0xFFB9C6DC

    override fun equippedBadgeNameRes(): Int? =
        equippedBadgeId()?.let { SampleData.item(it)?.nameRes }

    override fun equip(itemId: String) {
        val item = SampleData.item(itemId) ?: return
        if (!unlockedIds().contains(itemId)) return
        when (item.kind) {
            CollectibleKind.LIVERY ->
                prefs.putText(SkyPilotPreferences.KEY_EQUIPPED_LIVERY, itemId)
            CollectibleKind.BADGE ->
                prefs.putText(SkyPilotPreferences.KEY_EQUIPPED_BADGE, itemId)
        }
    }

    override fun refreshUnlocks(
        profile: PilotProfile,
        clearedStages: Int,
        trackCompleted: Boolean
    ): Int? {
        val unlocked = unlockedIds().toMutableSet()
        var firstNew: Int? = null
        SampleData.collectibles.forEach { item ->
            if (unlocked.contains(item.id)) return@forEach
            if (meetsRequirement(item, profile, clearedStages, trackCompleted)) {
                unlocked.add(item.id)
                if (firstNew == null) firstNew = item.nameRes
            }
        }
        if (firstNew != null) prefs.putIds(SkyPilotPreferences.KEY_UNLOCKED, unlocked)
        return firstNew
    }

    override fun unlockRandomLocked(roll: Float): Int? {
        val unlocked = unlockedIds().toMutableSet()
        val locked = SampleData.collectibles.filter { !unlocked.contains(it.id) }
        if (locked.isEmpty()) return null
        val pick = locked[(roll * locked.size).toInt().coerceIn(0, locked.size - 1)]
        unlocked.add(pick.id)
        prefs.putIds(SkyPilotPreferences.KEY_UNLOCKED, unlocked)
        return pick.nameRes
    }

    override fun resetAll() {
        prefs.putIds(SkyPilotPreferences.KEY_UNLOCKED, setOf(SampleData.DEFAULT_LIVERY))
        prefs.putText(SkyPilotPreferences.KEY_EQUIPPED_LIVERY, SampleData.DEFAULT_LIVERY)
        prefs.putText(SkyPilotPreferences.KEY_EQUIPPED_BADGE, "")
    }

    private fun unlockedIds(): Set<String> {
        val stored = prefs.ids(SkyPilotPreferences.KEY_UNLOCKED)
        return if (stored.isEmpty()) setOf(SampleData.DEFAULT_LIVERY) else stored
    }

    private fun equippedLiveryId(): String =
        prefs.text(SkyPilotPreferences.KEY_EQUIPPED_LIVERY, SampleData.DEFAULT_LIVERY)

    private fun equippedBadgeId(): String? =
        prefs.text(SkyPilotPreferences.KEY_EQUIPPED_BADGE, "").ifEmpty { null }

    private fun meetsRequirement(
        item: CollectibleItem,
        profile: PilotProfile,
        clearedStages: Int,
        trackCompleted: Boolean
    ): Boolean = when (item.unlockKind) {
        UnlockKind.START -> true
        UnlockKind.CLEAR_STAGE -> clearedStages >= item.unlockValue
        UnlockKind.OPEN_CRATES -> profile.cratesOpened >= item.unlockValue
        UnlockKind.REACH_MULTIPLIER -> profile.bestMultiplier >= item.unlockValue
        UnlockKind.BANK_RUNS -> profile.bankedRuns >= item.unlockValue
        UnlockKind.COLLECT_RINGS -> profile.ringsCollected >= item.unlockValue
        UnlockKind.FIRE_BOOSTS -> profile.boostsFired >= item.unlockValue
        UnlockKind.COMPLETE_TRACK -> trackCompleted
    }
}
