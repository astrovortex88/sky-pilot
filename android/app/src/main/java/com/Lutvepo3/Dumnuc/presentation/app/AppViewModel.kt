package com.Lutvepo3.Dumnuc.presentation.app

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.core.navigation.Screen
import com.Lutvepo3.Dumnuc.domain.repository.OnboardingRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    private val onboarding: OnboardingRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _reducedMotion = MutableStateFlow(settings.settings().reducedMotion)
    val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

    private val backStack = ArrayList<Screen>()

    fun completeSplash() {
        if (_screen.value != Screen.Splash) return
        backStack.clear()
        _screen.value = if (onboarding.isSeen()) Screen.Menu else Screen.Onboarding
        refreshMotion()
    }

    fun navigateTo(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) backStack.add(current)
        _screen.value = target
        refreshMotion()
    }

    fun replaceWith(target: Screen) {
        _screen.value = target
        refreshMotion()
    }

    fun goToMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        refreshMotion()
    }

    fun navigateBack(): Boolean {
        if (backStack.isEmpty()) return false
        val previous = backStack.removeAt(backStack.size - 1)
        _screen.value = previous
        refreshMotion()
        return true
    }

    private fun refreshMotion() {
        _reducedMotion.value = settings.settings().reducedMotion
    }
}
