package com.Lutvepo3.Dumnuc.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Onboarding : Screen
    data object Menu : Screen
    data class Game(val stageIndex: Int) : Screen
    data class GameOver(val resultId: Long) : Screen
    data object ProgressMap : Screen
    data object DailyGoals : Screen
    data object Collection : Screen
    data object Rewards : Screen
    data object Profile : Screen
    data object Settings : Screen
}
