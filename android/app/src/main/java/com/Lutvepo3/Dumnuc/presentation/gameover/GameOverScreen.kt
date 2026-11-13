package com.Lutvepo3.Dumnuc.presentation.gameover

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextSecondary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.HomeButton
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenBackground
import com.Lutvepo3.Dumnuc.core.ui.StatCard
import com.Lutvepo3.Dumnuc.core.ui.loopFloat

@Composable
fun GameOverScreen(
    onPlayAgain: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = ServiceLocator.factory())
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        gameOverViewModel.refresh()
    }

    GameOverContent(
        state = state,
        onPlayAgain = { onPlayAgain(gameOverViewModel.replayStage()) },
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val accent = Color(state.accent)
    val overlay = if (state.lost) {
        listOf(Color(0xCC2A1420), Color(0xE6101827), Color(0xF2101827))
    } else {
        listOf(Color(0xCC0F2234), Color(0xE6101827), Color(0xF2101827))
    }
    val transition = rememberInfiniteTransition(label = "resultDecor")
    val glow by transition.loopFloat(0.75f, 1.1f, 2800, label = "resultGlow")
    val liveGlow = if (state.reducedMotion) 1f else glow

    ScreenBackground(overlay = overlay, imageAlpha = 0.4f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = 240.dp.toPx() * liveGlow
            val center = Offset(size.width / 2f, size.height * 0.3f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.18f), Color.Transparent),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(state.bannerRes),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp),
                color = accent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )

            Text(
                text = if (state.lost) {
                    stringResource(R.string.result_note_lost)
                } else {
                    stringResource(R.string.result_note_won, state.pointsLabel)
                },
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            ResultCard(state = state, accent = accent)

            StageRibbon(state = state, accent = accent)

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                label = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                glyph = Glyph.REFRESH,
                height = 60.dp
            )
            HomeButton(
                label = stringResource(R.string.menu),
                onClick = onMenu
            )
        }
    }
}

@Composable
private fun ResultCard(state: GameOverUiState, accent: Color) {
    GlassCard(
        accent = accent,
        elevation = 20.dp,
        radius = 24.dp,
        padding = 18.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(
                    if (state.lost) R.drawable.sprite_3 else R.drawable.sprite_5
                ),
                contentDescription = stringResource(
                    if (state.lost) R.string.cd_storm else R.string.cd_badge
                ),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer { rotationZ = if (state.lost) 12f else 0f }
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.final_altitude, state.multiplierLabel),
                    style = MaterialTheme.typography.headlineSmall,
                    color = accent
                )
                Text(
                    text = stringResource(state.stageNameRes),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }

        StatRow(state = state)

        Text(
            text = if (state.assistApplied) {
                stringResource(
                    R.string.points_math_assist,
                    state.basePoints,
                    state.multiplierLabel,
                    state.streakLabel,
                    state.assistLabel,
                    state.pointsLabel
                )
            } else {
                stringResource(
                    R.string.points_math,
                    state.basePoints,
                    state.multiplierLabel,
                    state.streakLabel,
                    state.pointsLabel
                )
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Text(
            text = if (state.newBest) {
                stringResource(R.string.new_best)
            } else {
                stringResource(R.string.best_run, state.bestLabel)
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (state.newBest) Accent else TextMuted
        )
    }
}

@Composable
private fun StatRow(state: GameOverUiState) {
    val pills = ArrayList<Triple<Glyph, String, String>>()
    if (state.points > 0) {
        pills.add(
            Triple(
                Glyph.STAR,
                stringResource(R.string.points_gain, state.pointsLabel),
                stringResource(R.string.stat_points)
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
    pills.add(
        Triple(
            Glyph.TARGET,
            stringResource(R.string.multiplier_value, state.stageTargetLabel),
            stringResource(R.string.stat_best_altitude)
        )
    )
    if (pills.size < 2) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        pills.take(3).forEachIndexed { index, pill ->
            StatCard(
                glyph = pill.first,
                value = pill.second,
                label = pill.third,
                accent = if (index == 0) Accent else Secondary,
                compact = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StageRibbon(state: GameOverUiState, accent: Color) {
    val text = when {
        state.trackCompleted -> stringResource(R.string.track_complete)
        state.stageCleared -> stringResource(
            R.string.stage_ribbon_cleared,
            state.stageIndex,
            state.stageUnlocked
        )
        else -> stringResource(R.string.stage_ribbon_missed, state.stageTargetLabel)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (state.stageCleared) accent else TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceAlt.copy(alpha = 0.6f))
                .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .padding(vertical = 10.dp, horizontal = 12.dp)
        )
        if (state.crateEarned || state.unlockedItemNameRes != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.crateEarned) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Accent.copy(alpha = 0.18f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.crate_chip),
                            style = MaterialTheme.typography.labelSmall,
                            color = Accent
                        )
                    }
                }
                val unlocked = state.unlockedItemNameRes
                if (unlocked != null) {
                    Text(
                        text = stringResource(R.string.unlocked_item, stringResource(unlocked)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                }
            }
        }
    }
}
