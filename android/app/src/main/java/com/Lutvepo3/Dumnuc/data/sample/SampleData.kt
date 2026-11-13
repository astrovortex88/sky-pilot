package com.Lutvepo3.Dumnuc.data.sample

import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.domain.model.Callsign
import com.Lutvepo3.Dumnuc.domain.model.CollectibleItem
import com.Lutvepo3.Dumnuc.domain.model.CollectibleKind
import com.Lutvepo3.Dumnuc.domain.model.GoalKind
import com.Lutvepo3.Dumnuc.domain.model.GoalTemplate
import com.Lutvepo3.Dumnuc.domain.model.Rank
import com.Lutvepo3.Dumnuc.domain.model.Stage
import com.Lutvepo3.Dumnuc.domain.model.UnlockKind

object SampleData {

    val stages: List<Stage> = listOf(
        Stage(1, R.string.stage_1, GameConfig.stageTarget(1)),
        Stage(2, R.string.stage_2, GameConfig.stageTarget(2)),
        Stage(3, R.string.stage_3, GameConfig.stageTarget(3)),
        Stage(4, R.string.stage_4, GameConfig.stageTarget(4)),
        Stage(5, R.string.stage_5, GameConfig.stageTarget(5)),
        Stage(6, R.string.stage_6, GameConfig.stageTarget(6)),
        Stage(7, R.string.stage_7, GameConfig.stageTarget(7)),
        Stage(8, R.string.stage_8, GameConfig.stageTarget(8)),
        Stage(9, R.string.stage_9, GameConfig.stageTarget(9)),
        Stage(10, R.string.stage_10, GameConfig.stageTarget(10)),
        Stage(11, R.string.stage_11, GameConfig.stageTarget(11)),
        Stage(12, R.string.stage_12, GameConfig.stageTarget(12))
    )

    val goalPool: List<GoalTemplate> = listOf(
        GoalTemplate(GoalKind.BANK_RUNS, R.string.goal_bank_runs, 3, 120, 0),
        GoalTemplate(GoalKind.REACH_ALTITUDE, R.string.goal_reach_altitude, 1, 160, 0),
        GoalTemplate(GoalKind.COLLECT_RINGS, R.string.goal_collect_rings, 12, 140, 0),
        GoalTemplate(GoalKind.FIRE_BOOSTS, R.string.goal_fire_boosts, 2, 0, 1),
        GoalTemplate(GoalKind.CLEAR_STAGE, R.string.goal_clear_stage, 1, 0, 1),
        GoalTemplate(GoalKind.LAND_SAFE, R.string.goal_land_safe, 1, 110, 0),
        GoalTemplate(GoalKind.REACH_STREAK, R.string.goal_reach_streak, 1, 180, 0),
        GoalTemplate(GoalKind.FLY_SECONDS, R.string.goal_fly_seconds, 90, 130, 0)
    )

    val collectibles: List<CollectibleItem> = listOf(
        CollectibleItem(
            "harbor_grey", R.string.item_harbor_grey, CollectibleKind.LIVERY,
            0xFFB9C6DC, UnlockKind.START, 0, R.string.unlock_start
        ),
        CollectibleItem(
            "cyan_arrow", R.string.item_cyan_arrow, CollectibleKind.LIVERY,
            0xFF67E8F9, UnlockKind.BANK_RUNS, 3, R.string.unlock_bank_runs
        ),
        CollectibleItem(
            "gold_crest", R.string.item_gold_crest, CollectibleKind.LIVERY,
            0xFFF4D06F, UnlockKind.CLEAR_STAGE, 3, R.string.unlock_clear_stage
        ),
        CollectibleItem(
            "deep_blue", R.string.item_deep_blue, CollectibleKind.LIVERY,
            0xFF1D4ED8, UnlockKind.OPEN_CRATES, 2, R.string.unlock_open_crates
        ),
        CollectibleItem(
            "ember_trail", R.string.item_ember_trail, CollectibleKind.LIVERY,
            0xFFF4714F, UnlockKind.FIRE_BOOSTS, 3, R.string.unlock_fire_boosts
        ),
        CollectibleItem(
            "ivory_classic", R.string.item_ivory_classic, CollectibleKind.LIVERY,
            0xFFFFF8E7, UnlockKind.REACH_MULTIPLIER, 5, R.string.unlock_reach_multiplier
        ),
        CollectibleItem(
            "first_bank", R.string.item_first_bank, CollectibleKind.BADGE,
            0xFFF4D06F, UnlockKind.BANK_RUNS, 1, R.string.unlock_bank_runs
        ),
        CollectibleItem(
            "ring_hunter", R.string.item_ring_hunter, CollectibleKind.BADGE,
            0xFF67E8F9, UnlockKind.COLLECT_RINGS, 25, R.string.unlock_collect_rings
        ),
        CollectibleItem(
            "boost_chain", R.string.item_boost_chain, CollectibleKind.BADGE,
            0xFFFFD98A, UnlockKind.FIRE_BOOSTS, 5, R.string.unlock_fire_boosts
        ),
        CollectibleItem(
            "storm_dodger", R.string.item_storm_dodger, CollectibleKind.BADGE,
            0xFFF4714F, UnlockKind.CLEAR_STAGE, 5, R.string.unlock_clear_stage
        ),
        CollectibleItem(
            "stage_runner", R.string.item_stage_runner, CollectibleKind.BADGE,
            0xFF2A6BE8, UnlockKind.CLEAR_STAGE, 7, R.string.unlock_clear_stage
        ),
        CollectibleItem(
            "long_ascent", R.string.item_long_ascent, CollectibleKind.BADGE,
            0xFFFFF8E7, UnlockKind.COMPLETE_TRACK, 1, R.string.unlock_complete_track
        )
    )

    val callsigns: List<Callsign> = listOf(
        Callsign("nightjar", R.string.callsign_nightjar),
        Callsign("albatross", R.string.callsign_albatross),
        Callsign("kestrel", R.string.callsign_kestrel),
        Callsign("cirrus", R.string.callsign_cirrus),
        Callsign("magpie", R.string.callsign_magpie),
        Callsign("halcyon", R.string.callsign_halcyon),
        Callsign("tern", R.string.callsign_tern),
        Callsign("swift", R.string.callsign_swift)
    )

    const val DEFAULT_CALLSIGN = "nightjar"
    const val DEFAULT_LIVERY = "harbor_grey"

    fun stage(index: Int): Stage = stages[(index - 1).coerceIn(0, stages.size - 1)]

    fun callsignLabel(id: String): Int =
        callsigns.firstOrNull { it.id == id }?.labelRes ?: R.string.callsign_nightjar

    fun rankLabel(rank: Rank): Int = when (rank) {
        Rank.CADET -> R.string.rank_cadet
        Rank.PILOT -> R.string.rank_pilot
        Rank.ACE -> R.string.rank_ace
        Rank.CAPTAIN -> R.string.rank_captain
        Rank.SKY_MARSHAL -> R.string.rank_sky_marshal
    }

    fun item(id: String): CollectibleItem? = collectibles.firstOrNull { it.id == id }
}
