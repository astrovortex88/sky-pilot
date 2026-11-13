package com.Lutvepo3.Dumnuc.presentation.settings

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.core.di.AppDependencies
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val dependencies: AppDependencies
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val options = settings.settings()
        _state.update { current ->
            current.copy(
                controlSideLeft = options.controlSideLeft,
                assistMode = options.assistMode,
                autoCashOut = options.autoCashOut,
                reducedMotion = options.reducedMotion
            )
        }
    }

    fun setControlSideLeft(value: Boolean) {
        settings.setControlSideLeft(value)
        refresh()
    }

    fun setAssistMode(value: Boolean) {
        settings.setAssistMode(value)
        refresh()
    }

    fun setAutoCashOut(value: Float) {
        settings.setAutoCashOut(value)
        refresh()
    }

    fun setReducedMotion(value: Boolean) {
        settings.setReducedMotion(value)
        refresh()
    }

    fun askReset() {
        _state.update { current -> current.copy(showResetDialog = true) }
    }

    fun dismissReset() {
        _state.update { current -> current.copy(showResetDialog = false) }
    }

    fun confirmReset() {
        dependencies.wipeEverything()
        _state.update { current -> current.copy(showResetDialog = false) }
        refresh()
    }
}
