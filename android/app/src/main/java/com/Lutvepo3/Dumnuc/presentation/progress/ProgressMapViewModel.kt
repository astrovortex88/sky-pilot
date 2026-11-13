package com.Lutvepo3.Dumnuc.presentation.progress

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressMapViewModel(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressMapUiState())
    val state: StateFlow<ProgressMapUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val stages = progress.stages()
        val cleared = progress.clearedCount()
        _state.value = ProgressMapUiState(
            rows = stages.map { entry ->
                StageRow(
                    index = entry.stage.index,
                    nameRes = entry.stage.nameRes,
                    targetLabel = Formats.multiplier(entry.stage.target),
                    bestLabel = Formats.multiplier(entry.bestMultiplier),
                    hasBest = entry.bestMultiplier > 0f,
                    cleared = entry.cleared,
                    current = entry.current,
                    locked = entry.locked
                )
            },
            cleared = cleared,
            total = GameConfig.STAGE_COUNT,
            fraction = cleared.toFloat() / GameConfig.STAGE_COUNT,
            trackCompleted = progress.trackCompleted(),
            reducedMotion = settings.settings().reducedMotion
        )
    }

    fun selectStage(stageIndex: Int) {
        progress.selectStage(stageIndex)
        refresh()
    }
}
