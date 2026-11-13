package com.Lutvepo3.Dumnuc.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.domain.repository.OnboardingRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel(
    private val onboarding: OnboardingRepository,
    settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        OnboardingUiState(reducedMotion = settings.settings().reducedMotion)
    )
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun select(index: Int) {
        _state.update { it.copy(selected = index) }
    }

    fun markSeen() {
        onboarding.markSeen()
    }
}
