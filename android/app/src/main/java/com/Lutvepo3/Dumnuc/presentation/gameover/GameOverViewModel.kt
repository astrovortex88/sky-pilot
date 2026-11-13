package com.Lutvepo3.Dumnuc.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.domain.model.RunOutcome
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import com.Lutvepo3.Dumnuc.domain.usecase.BankRunUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val bankRun: BankRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val result = progress.lastResult()
        if (result == null) {
            _state.value = GameOverUiState(
                ready = false,
                reducedMotion = settings.settings().reducedMotion
            )
            return
        }
        val lost = result.outcome == RunOutcome.PLANE_LOST
        val banner = when (result.outcome) {
            RunOutcome.PLANE_LOST -> R.string.outcome_lost
            RunOutcome.SAFE_LANDING -> R.string.outcome_safe
            RunOutcome.STAGE_CLEARED -> R.string.outcome_cleared
            RunOutcome.BANKED -> R.string.outcome_banked
        }
        val accent = when (result.outcome) {
            RunOutcome.PLANE_LOST -> DANGER
            RunOutcome.SAFE_LANDING -> CYAN
            else -> GOLD
        }
        _state.value = GameOverUiState(
            ready = true,
            stageIndex = result.stageIndex,
            stageNameRes = result.stageNameRes,
            bannerRes = banner,
            accent = accent,
            lost = lost,
            multiplierLabel = Formats.multiplier(result.multiplier),
            pointsLabel = Formats.points(result.pointsEarned),
            points = result.pointsEarned,
            rings = result.rings,
            boosts = result.boosts,
            streakLabel = Formats.factor(result.streakFactor),
            assistLabel = Formats.factor(result.assistFactor),
            assistApplied = result.assistFactor > 1f,
            basePoints = result.basePoints,
            bestLabel = Formats.multiplier(result.bestMultiplier),
            newBest = result.newBest,
            stageCleared = result.stageCleared,
            stageUnlocked = result.stageUnlocked,
            stageTargetLabel = Formats.multiplier(result.stageTarget),
            crateEarned = result.crateEarned,
            trackCompleted = result.trackCompleted,
            unlockedItemNameRes = result.unlockedItemNameRes,
            reducedMotion = settings.settings().reducedMotion
        )
    }

    fun replayStage(): Int = progress.lastResult()?.stageIndex ?: progress.currentStage()

    fun previewPoints(multiplier: Float): Int =
        bankRun(multiplier, progress.bankedInARow(), settings.settings().assistMode)

    companion object {
        private const val GOLD = 0xFFF4D06F
        private const val CYAN = 0xFF67E8F9
        private const val DANGER = 0xFFF4714F
    }
}
