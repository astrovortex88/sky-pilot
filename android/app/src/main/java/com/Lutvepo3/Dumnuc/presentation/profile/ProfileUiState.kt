package com.Lutvepo3.Dumnuc.presentation.profile

import com.Lutvepo3.Dumnuc.R

data class CallsignChip(
    val id: String,
    val labelRes: Int,
    val selected: Boolean
)

data class StatEntry(
    val labelRes: Int,
    val value: String,
    val filled: Boolean
)

data class ProfileUiState(
    val callsignRes: Int = R.string.callsign_nightjar,
    val chips: List<CallsignChip> = emptyList(),
    val rankLabelRes: Int = R.string.rank_cadet,
    val rankIndex: Int = 0,
    val rankCount: Int = 5,
    val nextRankLabelRes: Int? = null,
    val pointsToNext: String = "",
    val badgeNameRes: Int? = null,
    val stats: List<StatEntry> = emptyList(),
    val filledStats: Int = 0
)
