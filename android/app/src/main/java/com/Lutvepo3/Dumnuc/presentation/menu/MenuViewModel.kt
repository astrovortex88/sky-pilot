package com.Lutvepo3.Dumnuc.presentation.menu

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.core.navigation.Screen
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository
import com.Lutvepo3.Dumnuc.domain.repository.GoalsRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProfileRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.RewardsRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import com.Lutvepo3.Dumnuc.domain.usecase.ResolveRankUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val progress: ProgressRepository,
    private val goals: GoalsRepository,
    private val collection: CollectionRepository,
    private val rewards: RewardsRepository,
    private val profile: ProfileRepository,
    private val settings: SettingsRepository,
    private val resolveRank: ResolveRankUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val pilot = profile.profile()
        val options = settings.settings()
        val stageIndex = progress.currentStage()
        val stage = SampleData.stage(stageIndex)
        val cleared = progress.clearedCount()
        val goalList = goals.goals()
        val goalsDone = goalList.count { it.claimed }
        val crates = rewards.stock()
        val unlocked = collection.unlockedCount()
        val rank = resolveRank(pilot.lifetimePoints)

        _state.value = MenuUiState(
            points = Formats.points(pilot.totalPoints),
            rankLabelRes = SampleData.rankLabel(rank),
            stageIndex = stageIndex,
            stageNameRes = stage.nameRes,
            stageTarget = Formats.multiplier(stage.target),
            clearedStages = cleared,
            totalStages = GameConfig.STAGE_COUNT,
            trackFraction = cleared.toFloat() / GameConfig.STAGE_COUNT,
            reducedMotion = options.reducedMotion,
            liveryColor = collection.equippedLiveryColor(),
            ready = true,
            tiles = listOf(
                MenuTile(
                    target = Screen.ProgressMap,
                    labelRes = R.string.tile_progress_map,
                    glyph = Glyph.MAP,
                    accent = CYAN,
                    value = TileValue(R.string.value_stages, listOf(cleared, GameConfig.STAGE_COUNT))
                ),
                MenuTile(
                    target = Screen.DailyGoals,
                    labelRes = R.string.tile_daily_goals,
                    glyph = Glyph.CALENDAR,
                    accent = GOLD,
                    value = if (goalsDone >= goalList.size && goalList.isNotEmpty()) {
                        TileValue(R.string.value_goals_clear)
                    } else {
                        TileValue(R.string.value_goals, listOf(goalsDone, goalList.size))
                    }
                ),
                MenuTile(
                    target = Screen.Collection,
                    labelRes = R.string.tile_collection,
                    glyph = Glyph.HEART,
                    accent = CYAN,
                    value = TileValue(
                        R.string.value_items,
                        listOf(unlocked, SampleData.collectibles.size)
                    )
                ),
                MenuTile(
                    target = Screen.Rewards,
                    labelRes = R.string.tile_rewards,
                    glyph = Glyph.CRATE,
                    accent = GOLD,
                    value = when {
                        crates.total <= 0 -> TileValue(R.string.value_no_crates)
                        crates.total == 1 -> TileValue(R.string.value_crate_one)
                        else -> TileValue(R.string.value_crates, listOf(crates.total))
                    }
                ),
                MenuTile(
                    target = Screen.Profile,
                    labelRes = R.string.tile_profile,
                    glyph = Glyph.PERSON,
                    accent = SILVER,
                    value = TileValue(SampleData.rankLabel(rank))
                ),
                MenuTile(
                    target = Screen.Settings,
                    labelRes = R.string.tile_settings,
                    glyph = Glyph.GEAR,
                    accent = SILVER,
                    value = TileValue(
                        if (options.assistMode) {
                            R.string.value_assist_on
                        } else {
                            R.string.value_assist_off
                        }
                    )
                )
            )
        )
    }

    companion object {
        private const val CYAN = 0xFF67E8F9
        private const val GOLD = 0xFFF4D06F
        private const val SILVER = 0xFFB9C6DC
    }
}
