package com.Lutvepo3.Dumnuc.presentation.rewards

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.domain.model.CrateTier
import com.Lutvepo3.Dumnuc.domain.repository.ProfileRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.RewardsRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class RewardsViewModel(
    private val rewards: RewardsRepository,
    private val progress: ProgressRepository,
    private val profile: ProfileRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RewardsUiState())
    val state: StateFlow<RewardsUiState> = _state.asStateFlow()

    private val random = Random(104729)

    init {
        refresh()
    }

    fun refresh() {
        val stock = rewards.stock()
        _state.update { current ->
            current.copy(
                standard = stock.standard,
                gold = stock.gold,
                total = stock.total,
                openedToday = stock.openedToday,
                pointsToday = Formats.points(stock.pointsToday),
                showToday = stock.openedToday > 0,
                reducedMotion = settings.settings().reducedMotion
            )
        }
    }

    fun open(tier: CrateTier) {
        val outcome = rewards.open(tier, random.nextFloat(), random.nextFloat())
        if (outcome.points <= 0) {
            refresh()
            return
        }
        progress.addPoints(outcome.points)
        profile.recordCrateOpened()
        refresh()
        _state.update { current ->
            current.copy(reveal = CrateReveal(outcome.points, outcome.itemNameRes))
        }
    }

    fun dismissReveal() {
        _state.update { current -> current.copy(reveal = null) }
        refresh()
    }
}
