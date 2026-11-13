package com.Lutvepo3.Dumnuc.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.ScreenScaffold
import com.Lutvepo3.Dumnuc.core.ui.StatCard

@Composable
fun ProfileScreen(onMenu: () -> Unit) {
    val profileViewModel: ProfileViewModel = viewModel(factory = ServiceLocator.factory())
    val state by profileViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileViewModel.refresh()
    }

    ProfileContent(
        state = state,
        onSelectCallsign = profileViewModel::selectCallsign,
        onMenu = onMenu
    )
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onSelectCallsign: (String) -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        title = stringResource(R.string.profile_title),
        subtitle = stringResource(R.string.profile_subtitle),
        menuLabel = stringResource(R.string.menu),
        onMenu = onMenu,
        accent = Accent
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item { PilotCard(state = state) }
            item { CallsignRow(state = state, onSelectCallsign = onSelectCallsign) }
            item { RankLadder(state = state) }
            item { StatGrid(state = state) }
        }
    }
}

@Composable
private fun PilotCard(state: ProfileUiState) {
    GlassCard(accent = Accent, elevation = 18.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceAlt.copy(alpha = 0.8f))
                    .border(1.dp, Accent.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(62.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(state.callsignRes),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(state.rankLabelRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = Accent
                )
                val badge = state.badgeNameRes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_badge),
                        contentScale = ContentScale.Fit,
                        alpha = if (badge == null) 0.35f else 1f,
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = if (badge == null) {
                            stringResource(R.string.badge_none)
                        } else {
                            stringResource(badge)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (badge == null) TextMuted else Secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun CallsignRow(state: ProfileUiState, onSelectCallsign: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.callsign_label),
            style = MaterialTheme.typography.labelMedium,
            color = Secondary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.chips, key = { chip -> chip.id }) { chip ->
                val label = stringResource(chip.labelRes)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (chip.selected) {
                                Accent.copy(alpha = 0.22f)
                            } else {
                                SurfaceAlt.copy(alpha = 0.7f)
                            }
                        )
                        .border(
                            1.dp,
                            if (chip.selected) Accent else Secondary.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectCallsign(chip.id) }
                        .padding(horizontal = 16.dp)
                        .semantics { stateDescription = label }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (chip.selected) Accent else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun RankLadder(state: ProfileUiState) {
    GlassCard(accent = Secondary, elevation = 12.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (index in 0 until state.rankCount) {
                Box(
                    modifier = Modifier
                        .size(if (index == state.rankIndex) 18.dp else 12.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= state.rankIndex) Accent else SurfaceAlt
                        )
                )
            }
        }
        val next = state.nextRankLabelRes
        Text(
            text = if (next == null) {
                stringResource(R.string.rank_max)
            } else {
                stringResource(R.string.rank_to_next, state.pointsToNext, stringResource(next))
            },
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun StatGrid(state: ProfileUiState) {
    if (state.filledStats < 2) {
        Text(
            text = stringResource(R.string.logbook_empty),
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }
    val glyphs = listOf(
        Glyph.STAR,
        Glyph.PLANE,
        Glyph.TARGET,
        Glyph.RING,
        Glyph.MAP,
        Glyph.BOLT
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        state.stats.chunked(2).forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEachIndexed { cellIndex, stat ->
                    val globalIndex = rowIndex * 2 + cellIndex
                    StatCard(
                        glyph = glyphs[globalIndex % glyphs.size],
                        value = if (stat.filled) stat.value else stringResource(R.string.empty_value),
                        label = stringResource(stat.labelRes),
                        accent = if (stat.filled) Accent else TextMuted,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
