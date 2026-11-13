package com.Lutvepo3.Dumnuc.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.data.sample.SampleData
import com.Lutvepo3.Dumnuc.domain.model.RunEvent
import com.Lutvepo3.Dumnuc.domain.model.RunOutcome
import com.Lutvepo3.Dumnuc.domain.model.RunResult
import com.Lutvepo3.Dumnuc.domain.model.RunState
import com.Lutvepo3.Dumnuc.domain.model.SpawnKind
import com.Lutvepo3.Dumnuc.domain.repository.CollectionRepository
import com.Lutvepo3.Dumnuc.domain.repository.GoalsRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProfileRepository
import com.Lutvepo3.Dumnuc.domain.repository.ProgressRepository
import com.Lutvepo3.Dumnuc.domain.repository.RewardsRepository
import com.Lutvepo3.Dumnuc.domain.repository.SettingsRepository
import com.Lutvepo3.Dumnuc.domain.usecase.BankRunUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.StartRunUseCase
import com.Lutvepo3.Dumnuc.domain.usecase.TickRunUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

sealed interface GameEvent {
    data class Finished(val resultId: Long) : GameEvent
}

class GameViewModel(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val collection: CollectionRepository,
    private val profile: ProfileRepository,
    private val goals: GoalsRepository,
    private val rewards: RewardsRepository,
    private val startRun: StartRunUseCase,
    private val tickRun: TickRunUseCase,
    private val bankRun: BankRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var run: RunState? = null
    private var loopJob: Job? = null
    private var prepared = -1

    fun prepare(stageIndex: Int) {
        if (prepared == stageIndex && _state.value.phase == GamePhase.FLYING) return
        prepared = stageIndex
        loopJob?.cancel()
        loopJob = null

        val options = settings.settings()
        val stage = SampleData.stage(stageIndex)
        val fresh = startRun(
            stageIndex = stageIndex,
            runIndex = progress.runIndex(),
            assistMode = options.assistMode,
            autoCashOut = options.autoCashOut
        )
        run = fresh
        _state.value = GameUiState(
            phase = GamePhase.IDLE,
            stageIndex = stageIndex,
            stageNameRes = stage.nameRes,
            stageTarget = Formats.multiplier(stage.target),
            multiplier = fresh.multiplier,
            multiplierLabel = Formats.multiplier(fresh.multiplier),
            fuelSeconds = Formats.seconds(fresh.fuelSec),
            fuelFraction = 1f,
            lanePos = fresh.lanePos,
            streakFactor = GameConfig.streakFactor(progress.bankedInARow()),
            controlSideLeft = options.controlSideLeft,
            reducedMotion = options.reducedMotion,
            liveryColor = collection.equippedLiveryColor(),
            hintVisible = true
        )
    }

    fun launchRun() {
        if (_state.value.phase != GamePhase.IDLE) return
        _state.update { it.copy(phase = GamePhase.FLYING) }
        startLoop()
    }

    fun onSkyTap(toLeft: Boolean) {
        val current = run ?: return
        if (_state.value.phase != GamePhase.FLYING) return
        val moved = tickRun.moveLane(current, toLeft)
        run = moved
        _state.update {
            it.copy(bankDegrees = if (toLeft) -14f else 14f)
        }
    }

    fun cashOut() {
        val current = run ?: return
        if (_state.value.phase != GamePhase.FLYING || !_state.value.cashOutEnabled) return
        val banked = current.copy(finished = true, outcome = RunOutcome.BANKED)
        run = banked
        finishRun(banked)
    }

    fun pause() {
        if (_state.value.phase != GamePhase.FLYING) return
        loopJob?.cancel()
        loopJob = null
        _state.update { it.copy(phase = GamePhase.PAUSED, paused = true) }
    }

    fun resume() {
        if (_state.value.phase != GamePhase.PAUSED) return
        _state.update { it.copy(phase = GamePhase.FLYING, paused = false) }
        startLoop()
    }

    fun restart() {
        loopJob?.cancel()
        loopJob = null
        val stage = prepared
        prepared = -1
        _state.update { it.copy(paused = false) }
        prepare(stage)
    }

    override fun onCleared() {
        loopJob?.cancel()
        loopJob = null
        super.onCleared()
    }

    private fun startLoop() {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            var last = System.nanoTime()
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val now = System.nanoTime()
                val deltaSec = ((now - last) / 1_000_000_000.0).toFloat().coerceIn(0f, 0.05f)
                last = now
                val current = run ?: break
                val outcome = tickRun(current, deltaSec)
                run = outcome.state
                publish(outcome.state, outcome.events, deltaSec)
                if (outcome.state.finished) {
                    finishRun(outcome.state)
                    break
                }
            }
        }
    }

    private fun publish(next: RunState, events: List<RunEvent>, deltaSec: Float) {
        val speed = GameConfig.worldSpeedUnitsPerSec(next.stageIndex)
        val visible = next.spawns.mapNotNull { spawn ->
            val y = GameConfig.PLANE_ROW_FRACTION - (spawn.arrivalSec - next.elapsedSec) * speed
            if (y < -0.18f || y > 1.2f) return@mapNotNull null
            if (spawn.resolved && y > GameConfig.PLANE_ROW_FRACTION + 0.12f) {
                return@mapNotNull null
            }
            SkyEntity(
                id = spawn.id,
                lane = spawn.lane,
                y = y,
                ring = spawn.kind == SpawnKind.RING,
                warning = next.assistMode &&
                    spawn.kind == SpawnKind.HAZARD &&
                    !spawn.resolved &&
                    spawn.arrivalSec - next.elapsedSec <= GameConfig.ASSIST_WARNING_SEC
            )
        }

        _state.update { current ->
            val decay = deltaSec * 3.4f
            val gold = if (events.contains(RunEvent.BOOST_FIRED)) {
                1f
            } else {
                (current.goldFlash - decay).coerceAtLeast(0f)
            }
            val danger = when {
                events.contains(RunEvent.PLANE_HIT) -> 1f
                events.contains(RunEvent.RECOVERED) -> 0.8f
                else -> (current.dangerFlash - decay).coerceAtLeast(0f)
            }
            val ring = if (events.contains(RunEvent.RING_COLLECTED)) {
                1f
            } else {
                (current.ringPulse - decay).coerceAtLeast(0f)
            }
            val bank = if (abs(next.lanePos - next.lane) < 0.02f) {
                current.bankDegrees * 0.82f
            } else {
                current.bankDegrees
            }
            current.copy(
                multiplier = next.multiplier,
                multiplierLabel = Formats.multiplier(next.multiplier),
                fuelSeconds = Formats.seconds(next.fuelSec),
                fuelFraction = (next.fuelSec / GameConfig.RUN_FUEL_SECONDS).coerceIn(0f, 1f),
                lanePos = next.lanePos,
                bankDegrees = bank,
                entities = visible,
                rings = next.rings,
                boosts = next.boosts,
                elapsedSeconds = next.elapsedSec.toInt(),
                elapsedRaw = next.elapsedSec,
                cashOutEnabled = next.elapsedSec >= GameConfig.CASH_OUT_LOCK_SEC,
                hintVisible = next.elapsedSec < GameConfig.CONTROL_HINT_SEC,
                boosting = next.boosting,
                recovering = next.recovering,
                goldFlash = gold,
                dangerFlash = danger,
                ringPulse = ring
            )
        }
    }

    private fun finishRun(finished: RunState) {
        loopJob?.cancel()
        loopJob = null
        if (_state.value.phase == GamePhase.FINISHED) return
        _state.update { it.copy(phase = GamePhase.FINISHED, paused = false) }

        val stage = SampleData.stage(finished.stageIndex)
        val target = GameConfig.stageTarget(finished.stageIndex)
        val lost = finished.outcome == RunOutcome.PLANE_LOST
        val bankedInARow = progress.bankedInARow()
        val streakFactor = GameConfig.streakFactor(bankedInARow)
        val assistFactor = bankRun.assistFactor(finished.assistMode)
        val points = if (lost) {
            0
        } else {
            bankRun(finished.multiplier, bankedInARow, finished.assistMode)
        }
        val alreadyCleared = progress.stages()
            .firstOrNull { it.stage.index == finished.stageIndex }?.cleared == true
        val cleared = !lost && finished.multiplier >= target
        val outcome = when {
            lost -> RunOutcome.PLANE_LOST
            cleared -> RunOutcome.STAGE_CLEARED
            else -> finished.outcome ?: RunOutcome.BANKED
        }
        val crateEarned = cleared && !alreadyCleared
        val previousBest = progress.bestMultiplier()

        val result = RunResult(
            id = System.currentTimeMillis(),
            stageIndex = finished.stageIndex,
            stageNameRes = stage.nameRes,
            outcome = outcome,
            multiplier = finished.multiplier,
            stageTarget = target,
            basePoints = GameConfig.RUN_BASE_POINTS,
            streakFactor = streakFactor,
            assistFactor = assistFactor,
            pointsEarned = points,
            rings = finished.rings,
            boosts = finished.boosts,
            durationSec = finished.elapsedSec,
            stageCleared = cleared,
            stageUnlocked = (finished.stageIndex + 1).coerceAtMost(GameConfig.STAGE_COUNT),
            trackCompleted = false,
            crateEarned = crateEarned,
            newBest = finished.multiplier > previousBest,
            bestMultiplier = maxOf(previousBest, finished.multiplier),
            seed = finished.seed,
            unlockedItemNameRes = null
        )

        progress.applyRun(result)
        profile.recordRun(result, bankedInARow)
        goals.recordRun(result, bankedInARow)
        if (crateEarned) rewards.grantCrates(standard = 1, gold = 0)
        val unlocked = collection.refreshUnlocks(
            profile.profile(),
            progress.clearedCount(),
            progress.trackCompleted()
        )
        val stored = result.copy(
            trackCompleted = progress.trackCompleted(),
            unlockedItemNameRes = unlocked
        )
        progress.storeResult(stored)

        viewModelScope.launch {
            eventChannel.send(GameEvent.Finished(stored.id))
        }
    }
}
