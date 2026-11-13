package com.Lutvepo3.Dumnuc.presentation.progress

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.PrimaryGradient
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.GlyphIcon
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenScaffold
import com.Lutvepo3.Dumnuc.core.ui.SectionProgressBar
import com.Lutvepo3.Dumnuc.core.ui.loopFloat

@Composable
fun ProgressMapScreen(
    onFlyStage: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val progressViewModel: ProgressMapViewModel = viewModel(factory = ServiceLocator.factory())
    val state by progressViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        progressViewModel.refresh()
    }

    ProgressMapContent(
        state = state,
        onFlyStage = { stage ->
            progressViewModel.selectStage(stage)
            onFlyStage(stage)
        },
        onMenu = onMenu
    )
}

@Composable
private fun ProgressMapContent(
    state: ProgressMapUiState,
    onFlyStage: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "mapDecor")
    val pulse by transition.loopFloat(1f, 1.08f, 1600, label = "nodePulse")
    val livePulse = if (state.reducedMotion) 1f else pulse

    ScreenScaffold(
        title = stringResource(R.string.progress_title),
        subtitle = stringResource(R.string.progress_subtitle, state.cleared, state.total),
        menuLabel = stringResource(R.string.menu),
        onMenu = onMenu,
        accent = Secondary
    ) {
        SectionProgressBar(progress = state.fraction, accent = Secondary)

        if (state.trackCompleted) {
            Text(
                text = stringResource(R.string.track_complete),
                style = MaterialTheme.typography.labelMedium,
                color = Accent
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.rows, key = { row -> row.index }) { row ->
                StageNode(row = row, pulse = livePulse, onFlyStage = onFlyStage)
            }
        }
    }
}

@Composable
private fun StageNode(
    row: StageRow,
    pulse: Float,
    onFlyStage: (Int) -> Unit
) {
    val accent = when {
        row.current -> Accent
        row.cleared -> Secondary
        else -> TextMuted
    }
    GlassCard(
        accent = accent,
        elevation = if (row.current) 18.dp else 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        val grow = if (row.current) pulse else 1f
                        scaleX = grow
                        scaleY = grow
                    }
                    .clip(CircleShape)
                    .background(
                        if (row.cleared) {
                            Brush.linearGradient(PrimaryGradient)
                        } else {
                            Brush.linearGradient(listOf(SurfaceAlt, SurfaceAlt))
                        }
                    )
                    .border(if (row.current) 3.dp else 1.dp, accent.copy(alpha = 0.7f), CircleShape)
            ) {
                when {
                    row.cleared -> GlyphIcon(Glyph.CHECK, TextPrimary, size = 28.dp)
                    row.current -> Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(36.dp)
                    )
                    else -> GlyphIcon(Glyph.LOCK, TextMuted, size = 24.dp)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.stage_number, row.index),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
                Text(
                    text = stringResource(row.nameRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (row.locked) TextMuted else TextPrimary
                )
                Text(
                    text = stringResource(R.string.stage_target, row.targetLabel),
                    style = MaterialTheme.typography.labelSmall
                )
                if (row.hasBest) {
                    Text(
                        text = stringResource(R.string.stage_best, row.bestLabel),
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                }
            }
        }

        if (row.current) {
            PrimaryButton(
                label = stringResource(R.string.fly_stage, row.index),
                onClick = { onFlyStage(row.index) },
                glyph = Glyph.PLANE,
                height = 52.dp
            )
        }
        if (row.locked) {
            Text(
                text = stringResource(R.string.locked_hint, row.index - 1),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
