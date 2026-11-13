package com.Lutvepo3.Dumnuc.presentation.menu

import com.Lutvepo3.Dumnuc.core.navigation.Screen
import com.Lutvepo3.Dumnuc.core.ui.Glyph

data class TileValue(
    val res: Int,
    val args: List<Int> = emptyList()
)

data class MenuTile(
    val target: Screen,
    val labelRes: Int,
    val glyph: Glyph,
    val accent: Long,
    val value: TileValue
)

data class MenuUiState(
    val points: String = "",
    val rankLabelRes: Int = 0,
    val stageIndex: Int = 1,
    val stageNameRes: Int = 0,
    val stageTarget: String = "",
    val clearedStages: Int = 0,
    val totalStages: Int = 12,
    val trackFraction: Float = 0f,
    val tiles: List<MenuTile> = emptyList(),
    val reducedMotion: Boolean = false,
    val liveryColor: Long = 0xFFB9C6DC,
    val ready: Boolean = false
)
