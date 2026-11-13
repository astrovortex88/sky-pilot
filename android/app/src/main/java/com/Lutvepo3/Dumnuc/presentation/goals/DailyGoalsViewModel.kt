package com.Lutvepo3.Dumnuc.presentation.goals

import androidx.lifecycle.ViewModel
import com.Lutvepo3.Dumnuc.domain.repository.GoalsRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.RewardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DailyGoalsViewModel(
    private val goals: GoalsRepository,
    private val progress: ProgressRepository,
    private val rewards: RewardsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DailyGoalsUiState())
    val state: StateFlow<DailyGoalsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val list = goals.goals()
        val minutes = goals.resetsInMinutes()
        _state.value = DailyGoalsUiState(
            rows = list.mapIndexed { index, goal ->
                GoalRow(
                    index = index,
                    titleRes = goal.titleRes,
                    progress = goal.progress,
                    target = goal.target,
                    fraction = goal.fraction,
                    rewardPoints = goal.rewardPoints,
                    rewardCrates = goal.rewardCrates,
                    complete = goal.complete,
                    claimed = goal.claimed
                )
            },
            resetHours = minutes / 60,
            resetMinutes = minutes % 60,
            allClaimed = list.isNotEmpty() && list.all { it.claimed }
        )
    }

    fun claim(index: Int) {
        val goal = goals.claim(index) ?: return
        if (goal.rewardPoints > 0) progress.addPoints(goal.rewardPoints)
        if (goal.rewardCrates > 0) rewards.grantCrates(standard = 0, gold = goal.rewardCrates)
        refresh()
    }
}
