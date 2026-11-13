package com.Lutvepo3.Dumnuc.data.repository

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.data.local.SkyPilotPreferences
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.model.DailyGoal
import com.Lutvepo3.Dumnuc.domain.model.GoalTemplate
import com.Lutvepo3.Dumnuc.domain.model.RunResult
import com.Lutvepo3.Dumnuc.domain.repository.GoalsRepository
import com.Lutvepo3.Dumnuc.domain.usecase.EvaluateGoalsUseCase
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class GoalsRepositoryImpl(
    private val prefs: SkyPilotPreferences,
    private val evaluateGoals: EvaluateGoalsUseCase
) : GoalsRepository {

    override fun goals(): List<DailyGoal> {
        syncDay()
        return templates().mapIndexed { index, template ->
            DailyGoal(
                kind = template.kind,
                titleRes = template.titleRes,
                target = template.target,
                progress = prefs.int(SkyPilotPreferences.KEY_GOAL_PROGRESS + index, 0)
                    .coerceAtMost(template.target),
                rewardPoints = template.rewardPoints,
                rewardCrates = template.rewardCrates,
                claimed = prefs.bool(SkyPilotPreferences.KEY_GOAL_CLAIMED + index, false)
            )
        }
    }

    override fun resetsInMinutes(): Int {
        val now = LocalTime.now()
        return MINUTES_PER_DAY - (now.hour * 60 + now.minute)
    }

    override fun recordRun(result: RunResult, bankedInARow: Int) {
        syncDay()
        templates().forEachIndexed { index, template ->
            val gain = evaluateGoals(template.kind, result, bankedInARow)
            if (gain <= 0) return@forEachIndexed
            val key = SkyPilotPreferences.KEY_GOAL_PROGRESS + index
            val value = (prefs.int(key, 0) + gain).coerceAtMost(template.target)
            prefs.putInt(key, value)
        }
    }

    override fun claim(index: Int): DailyGoal? {
        val goal = goals().getOrNull(index) ?: return null
        if (!goal.complete || goal.claimed) return null
        prefs.putBool(SkyPilotPreferences.KEY_GOAL_CLAIMED + index, true)
        return goal
    }

    override fun resetAll() {
        prefs.putLong(SkyPilotPreferences.KEY_GOAL_DAY, today())
        clearSlots()
    }

    private fun templates(): List<GoalTemplate> =
        SampleData.goalPool.shuffled(Random(today())).take(GameConfig.DAILY_GOAL_SLOTS)

    private fun today(): Long = LocalDate.now().toEpochDay()

    private fun syncDay() {
        val stored = prefs.long(SkyPilotPreferences.KEY_GOAL_DAY, -1L)
        val now = today()
        if (stored == now) return
        prefs.putLong(SkyPilotPreferences.KEY_GOAL_DAY, now)
        clearSlots()
    }

    private fun clearSlots() {
        for (index in 0 until GameConfig.DAILY_GOAL_SLOTS) {
            prefs.putInt(SkyPilotPreferences.KEY_GOAL_PROGRESS + index, 0)
            prefs.putBool(SkyPilotPreferences.KEY_GOAL_CLAIMED + index, false)
        }
    }

    companion object {
        private const val MINUTES_PER_DAY = 24 * 60
    }
}
