package com.Lutvepo3.Dumnuc.domain.usecase

import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.domain.model.RunEvent
import com.Lutvepo3.Dumnuc.domain.model.RunOutcome
import com.Lutvepo3.Dumnuc.domain.model.RunState
import com.Lutvepo3.Dumnuc.domain.model.SpawnKind
import com.Lutvepo3.Dumnuc.domain.model.TickResult
import kotlin.math.abs
import kotlin.math.sign

class TickRunUseCase {

    operator fun invoke(state: RunState, deltaSec: Float): TickResult {
        if (state.finished) return TickResult(state, emptyList())

        val events = ArrayList<RunEvent>()
        val elapsed = state.elapsedSec + deltaSec

        val climb = if (elapsed < GameConfig.CLIMB_SWITCH_SEC) {
            GameConfig.CLIMB_EARLY_PER_SEC
        } else {
            GameConfig.CLIMB_LATE_PER_SEC
        }

        var multiplier = state.multiplier + climb * deltaSec
        val ceiling = GameConfig.fuelCeiling(elapsed)
        var fuel = (state.fuelSec - deltaSec).coerceIn(0f, ceiling)
        var rings = state.rings
        var boosts = state.boosts
        var chain = state.chain
        var invulnUntil = state.invulnUntilSec
        var recoveringUntil = state.recoveringUntilSec
        var recoveryUsed = state.recoveryUsed
        var outcome: RunOutcome? = null

        val step = deltaSec / GameConfig.LANE_SHIFT_SEC
        val target = state.lane.toFloat()
        val lanePos = if (abs(target - state.lanePos) <= step) {
            target
        } else {
            state.lanePos + sign(target - state.lanePos) * step
        }

        val spawns = state.spawns.toMutableList()
        for (index in spawns.indices) {
            val spawn = spawns[index]
            if (spawn.resolved || elapsed < spawn.arrivalSec) continue
            val onLane = abs(lanePos - spawn.lane) < 0.5f
            spawns[index] = spawn.copy(resolved = true)
            if (!onLane) {
                if (spawn.kind == SpawnKind.RING) chain = 0
                continue
            }
            if (spawn.kind == SpawnKind.RING) {
                rings += 1
                chain += 1
                multiplier += GameConfig.RING_MULTIPLIER_BONUS
                fuel = (fuel + GameConfig.RING_FUEL_BONUS_SEC)
                    .coerceAtMost(ceiling)
                events.add(RunEvent.RING_COLLECTED)
                if (chain >= GameConfig.BOOST_CHAIN_LENGTH) {
                    chain = 0
                    boosts += 1
                    multiplier += GameConfig.BOOST_MULTIPLIER_BONUS
                    invulnUntil = elapsed + GameConfig.BOOST_INVULN_SEC
                    events.add(RunEvent.BOOST_FIRED)
                }
            } else {
                if (elapsed < invulnUntil) continue
                val canRecover = state.assistMode && !recoveryUsed && rings >= 1
                if (canRecover) {
                    recoveryUsed = true
                    recoveringUntil = elapsed + GameConfig.ASSIST_RECOVERY_SEC
                    multiplier = (multiplier - GameConfig.ASSIST_RECOVERY_PENALTY)
                        .coerceAtLeast(GameConfig.START_MULTIPLIER)
                    chain = 0
                    events.add(RunEvent.RECOVERED)
                } else {
                    outcome = RunOutcome.PLANE_LOST
                    events.add(RunEvent.PLANE_HIT)
                }
            }
            if (outcome != null) break
        }

        if (outcome == null && state.autoCashOut > 0f &&
            elapsed >= GameConfig.CASH_OUT_LOCK_SEC && multiplier >= state.autoCashOut
        ) {
            outcome = RunOutcome.BANKED
            events.add(RunEvent.AUTO_BANK)
        }

        if (outcome == null && fuel <= 0f) {
            outcome = RunOutcome.SAFE_LANDING
            events.add(RunEvent.FUEL_EMPTY)
        }

        val next = state.copy(
            elapsedSec = elapsed,
            multiplier = multiplier,
            fuelSec = fuel,
            lanePos = lanePos,
            rings = rings,
            boosts = boosts,
            chain = chain,
            invulnUntilSec = invulnUntil,
            recoveringUntilSec = recoveringUntil,
            recoveryUsed = recoveryUsed,
            spawns = spawns,
            finished = outcome != null,
            outcome = outcome
        )
        return TickResult(next, events)
    }

    fun moveLane(state: RunState, toLeft: Boolean): RunState {
        if (state.finished) return state
        val next = (state.lane + if (toLeft) -1 else 1)
            .coerceIn(0, GameConfig.LANE_COUNT - 1)
        if (next == state.lane) return state
        return state.copy(lane = next)
    }
}
