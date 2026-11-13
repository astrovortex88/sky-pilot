package com.Lutvepo3.Dumnuc.presentation.profile

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.model.Rank
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProfileRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.usecase.ResolveRankUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
    private val profile: ProfileRepository,
    private val progress: ProgressRepository,
    private val collection: CollectionRepository,
    private val resolveRank: ResolveRankUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val pilot = profile.profile()
        val rank = resolveRank(pilot.lifetimePoints)
        val next = resolveRank.next(rank)
        val stats = listOf(
            entry(R.string.stat_total_sp, Formats.points(pilot.totalPoints), pilot.totalPoints > 0),
            entry(R.string.stat_runs, pilot.runsFlown.toString(), pilot.runsFlown > 0),
            entry(
                R.string.stat_best_altitude,
                Formats.multiplier(pilot.bestMultiplier),
                pilot.bestMultiplier > 0f
            ),
            entry(R.string.stat_rings_total, pilot.ringsCollected.toString(), pilot.ringsCollected > 0),
            entry(R.string.stat_stages, progress.clearedCount().toString(), progress.clearedCount() > 0),
            entry(
                R.string.stat_best_streak,
                Formats.factor(com.Lutvepo3.Dumnuc.core.config.GameConfig.streakFactor(pilot.bestStreak)),
                pilot.bestStreak > 0
            )
        )
        _state.value = ProfileUiState(
            callsignRes = SampleData.callsignLabel(pilot.callsignId),
            chips = SampleData.callsigns.map { callsign ->
                CallsignChip(
                    id = callsign.id,
                    labelRes = callsign.labelRes,
                    selected = callsign.id == pilot.callsignId
                )
            },
            rankLabelRes = SampleData.rankLabel(rank),
            rankIndex = Rank.entries.indexOf(rank),
            rankCount = Rank.entries.size,
            nextRankLabelRes = next?.let { SampleData.rankLabel(it) },
            pointsToNext = Formats.points(resolveRank.pointsToNext(pilot.lifetimePoints)),
            badgeNameRes = collection.equippedBadgeNameRes(),
            stats = stats,
            filledStats = stats.count { it.filled }
        )
    }

    fun selectCallsign(id: String) {
        profile.setCallsign(id)
        refresh()
    }

    private fun entry(labelRes: Int, value: String, filled: Boolean): StatEntry =
        StatEntry(labelRes, value, filled)
}
