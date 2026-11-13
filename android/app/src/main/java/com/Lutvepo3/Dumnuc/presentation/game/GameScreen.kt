package com.Lutvepo3.Dumnuc.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.Danger
import com.Lutvepo3.Dumnuc.core.theme.GoldGradient
import com.Lutvepo3.Dumnuc.core.theme.PrimaryGradient
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.Formats
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenBackground
import com.Lutvepo3.Dumnuc.core.ui.StatCard
import com.Lutvepo3.Dumnuc.presentation.dialog.PauseDialog

private val GameOverlay = listOf(
    Color(0x99101827),
    Color(0x3316223A),
    Color(0xCC101827)
)

@Composable
fun GameScreen(
    stageIndex: Int,
    onFinished: (Long) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.factory())
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(stageIndex) {
        gameViewModel.prepare(stageIndex)
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.resultId)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameContent(
        state = state,
        onSkyTap = { toLeft ->
            if (state.phase == GamePhase.IDLE) {
                gameViewModel.launchRun()
            } else {
                gameViewModel.onSkyTap(toLeft)
            }
        },
        onAction = {
            if (state.phase == GamePhase.IDLE) {
                gameViewModel.launchRun()
            } else {
                gameViewModel.cashOut()
            }
        },
        onPause = gameViewModel::pause,
        onResume = gameViewModel::resume,
        onRestart = gameViewModel::restart,
        onMenu = onMenu
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onSkyTap: (Boolean) -> Unit,
    onAction: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenBackground(overlay = GameOverlay, imageAlpha = 0.5f) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(state = state, onPause = onPause)
            Box(modifier = Modifier.weight(1f)) {
                SkyLane(state = state, onTap = onSkyTap)
                FloatingBar(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        }
    }

    if (state.phase == GamePhase.PAUSED) {
        PauseDialog(
            onResume = onResume,
            onRestart = onRestart,
            onMenu = onMenu
        )
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    val tint = when {
        state.multiplier >= 4f -> Accent
        state.multiplier >= 2f -> TextPrimary
        else -> Secondary
    }
    val altitudeState = stringResource(R.string.state_altitude, state.multiplierLabel)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(Color(0xFF101827).copy(alpha = 0.55f))
            .padding(horizontal = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceAlt.copy(alpha = 0.75f))
                .clickable(onClick = onPause)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = altitudeState
            }
        ) {
            Text(
                text = stringResource(R.string.altitude),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = stringResource(R.string.multiplier_value, state.multiplierLabel),
                style = MaterialTheme.typography.headlineMedium,
                color = tint
            )
        }

        FuelGauge(state = state)
    }
}

@Composable
private fun FuelGauge(state: GameUiState) {
    val low = state.fuelFraction < 0.25f
    val tint = if (low) Danger else Secondary
    val fuelState = stringResource(R.string.state_fuel, state.fuelSeconds)
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.semantics {
            stateDescription = fuelState
        }
    ) {
        Text(
            text = stringResource(R.string.fuel_value, state.fuelSeconds),
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SurfaceAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(state.fuelFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tint)
            )
        }
    }
}

@Composable
private fun FloatingBar(
    state: GameUiState,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val idle = state.phase == GamePhase.IDLE
    val label = if (idle) {
        stringResource(R.string.launch)
    } else {
        stringResource(R.string.cash_out, state.multiplierLabel)
    }
    val enabled = idle || state.cashOutEnabled
    val status = if (enabled) {
        stringResource(R.string.state_cash_ready, state.multiplierLabel)
    } else {
        stringResource(R.string.state_cash_locked)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        StatStrip(state = state)
        PrimaryButton(
            label = label,
            onClick = onAction,
            glyph = if (idle) Glyph.PLAY else Glyph.CHECK,
            colors = if (idle) PrimaryGradient else GoldGradient,
            contentColor = if (idle) TextPrimary else Color(0xFF101827),
            enabled = enabled,
            height = 64.dp,
            status = status
        )
    }
}

@Composable
private fun StatStrip(state: GameUiState) {
    val pills = ArrayList<Triple<Glyph, String, String>>()
    pills.add(
        Triple(
            Glyph.MAP,
            state.stageIndex.toString(),
            stringResource(state.stageNameRes)
        )
    )
    if (state.elapsedSeconds > 0) {
        pills.add(
            Triple(
                Glyph.CLOCK,
                stringResource(R.string.fuel_value, state.elapsedSeconds),
                stringResource(R.string.stat_run)
            )
        )
    }
    if (state.rings > 0) {
        pills.add(
            Triple(Glyph.RING, state.rings.toString(), stringResource(R.string.stat_rings))
        )
    }
    if (state.boosts > 0) {
        pills.add(
            Triple(Glyph.BOLT, state.boosts.toString(), stringResource(R.string.boost))
        )
    }
    if (state.streakFactor > 1f) {
        pills.add(
            Triple(
                Glyph.STAR,
                stringResource(R.string.streak_value, Formats.factor(state.streakFactor)),
                stringResource(R.string.stat_streak)
            )
        )
    }
    if (pills.size < 2) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        pills.take(4).forEachIndexed { index, pill ->
            StatCard(
                glyph = pill.first,
                value = pill.second,
                label = pill.third,
                accent = if (index == 0) Secondary else Accent,
                compact = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
