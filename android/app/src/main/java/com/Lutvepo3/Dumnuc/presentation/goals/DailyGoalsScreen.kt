package com.Lutvepo3.Dumnuc.presentation.goals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.GoldGradient
import com.Lutvepo3.Dumnuc.core.theme.BgDeep
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.GlyphIcon
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenScaffold
import com.Lutvepo3.Dumnuc.core.ui.SectionProgressBar

@Composable
fun DailyGoalsScreen(onMenu: () -> Unit) {
    val goalsViewModel: DailyGoalsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by goalsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        goalsViewModel.refresh()
    }

    DailyGoalsContent(
        state = state,
        onClaim = goalsViewModel::claim,
        onMenu = onMenu
    )
}

@Composable
private fun DailyGoalsContent(
    state: DailyGoalsUiState,
    onClaim: (Int) -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        title = stringResource(R.string.goals_title),
        subtitle = stringResource(
            R.string.goals_subtitle,
            state.resetHours,
            state.resetMinutes
        ),
        menuLabel = stringResource(R.string.menu),
        onMenu = onMenu,
        accent = Accent
    ) {
        if (state.allClaimed) {
            EmptyGoals()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.rows, key = { row -> row.index }) { row ->
                    GoalCard(row = row, onClaim = onClaim)
                }
            }
        }
    }
}

@Composable
private fun GoalCard(row: GoalRow, onClaim: (Int) -> Unit) {
    val accent = if (row.complete) Accent else Secondary
    val status = stringResource(R.string.goal_progress, row.progress, row.target)
    GlassCard(
        accent = accent,
        elevation = if (row.complete) 16.dp else 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = status }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.16f))
            ) {
                GlyphIcon(
                    glyph = if (row.complete) Glyph.CHECK else Glyph.TARGET,
                    tint = accent,
                    size = 24.dp
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(row.titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (row.claimed) TextMuted else TextPrimary
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.16f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (row.rewardCrates > 0) {
                        stringResource(R.string.reward_crate)
                    } else {
                        stringResource(R.string.reward_points, row.rewardPoints)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }

        SectionProgressBar(progress = row.fraction, accent = accent)

        if (row.claimed) {
            Text(
                text = stringResource(R.string.claimed),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted
            )
        } else if (row.complete) {
            PrimaryButton(
                label = stringResource(R.string.claim),
                onClick = { onClaim(row.index) },
                glyph = Glyph.STAR,
                colors = GoldGradient,
                contentColor = BgDeep,
                height = 50.dp
            )
        }
    }
}

@Composable
private fun EmptyGoals() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_5),
            contentDescription = stringResource(R.string.cd_badge),
            contentScale = ContentScale.Fit,
            alpha = 0.6f,
            modifier = Modifier.size(96.dp)
        )
        Text(
            text = stringResource(R.string.goals_empty),
            style = MaterialTheme.typography.labelMedium,
            color = Accent,
            textAlign = TextAlign.Center
        )
    }
}
