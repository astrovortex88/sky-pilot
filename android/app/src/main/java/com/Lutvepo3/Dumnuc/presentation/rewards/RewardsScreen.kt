package com.Lutvepo3.Dumnuc.presentation.rewards

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.BgDeep
import com.Lutvepo3.Dumnuc.core.theme.GoldGradient
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenScaffold
import com.Lutvepo3.Dumnuc.core.ui.loopFloat
import com.Lutvepo3.Dumnuc.domain.model.CrateTier
import com.Lutvepo3.Dumnuc.presentation.dialog.CrateOpenDialog

@Composable
fun RewardsScreen(onMenu: () -> Unit) {
    val rewardsViewModel: RewardsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by rewardsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        rewardsViewModel.refresh()
    }

    RewardsContent(
        state = state,
        onOpen = rewardsViewModel::open,
        onCollect = rewardsViewModel::dismissReveal,
        onMenu = onMenu
    )
}

@Composable
private fun RewardsContent(
    state: RewardsUiState,
    onOpen: (CrateTier) -> Unit,
    onCollect: () -> Unit,
    onMenu: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "crateDecor")
    val hover by transition.loopFloat(-4f, 4f, 1500, label = "crateHover")
    val live = if (state.reducedMotion) 0f else hover

    ScreenScaffold(
        title = stringResource(R.string.rewards_title),
        subtitle = stringResource(R.string.rewards_subtitle, state.total),
        menuLabel = stringResource(R.string.menu),
        onMenu = onMenu,
        accent = Accent
    ) {
        if (state.total <= 0) {
            EmptyRewards(hover = live)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.standard > 0) {
                    item {
                        CrateCard(
                            titleRes = R.string.crate_standard,
                            hintRes = R.string.crate_standard_hint,
                            count = state.standard,
                            hover = live,
                            onOpen = { onOpen(CrateTier.STANDARD) }
                        )
                    }
                }
                if (state.gold > 0) {
                    item {
                        CrateCard(
                            titleRes = R.string.crate_gold,
                            hintRes = R.string.crate_gold_hint,
                            count = state.gold,
                            hover = live,
                            onOpen = { onOpen(CrateTier.GOLD) }
                        )
                    }
                }
                if (state.showToday) {
                    item {
                        Text(
                            text = stringResource(
                                R.string.crate_today,
                                state.openedToday,
                                state.pointsToday
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = Secondary
                        )
                    }
                }
            }
        }
    }

    val reveal = state.reveal
    if (reveal != null) {
        CrateOpenDialog(
            points = reveal.points,
            itemNameRes = reveal.itemNameRes,
            reducedMotion = state.reducedMotion,
            onCollect = onCollect
        )
    }
}

@Composable
private fun CrateCard(
    titleRes: Int,
    hintRes: Int,
    count: Int,
    hover: Float,
    onOpen: () -> Unit
) {
    GlassCard(
        accent = Accent,
        elevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = stringResource(R.string.cd_crate),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer { translationY = hover }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(hintRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = stringResource(R.string.crate_count, count),
                    style = MaterialTheme.typography.titleMedium,
                    color = Accent
                )
            }
        }
        PrimaryButton(
            label = stringResource(R.string.open_crate),
            onClick = onOpen,
            glyph = Glyph.CRATE,
            colors = GoldGradient,
            contentColor = BgDeep,
            height = 52.dp
        )
    }
}

@Composable
private fun EmptyRewards(hover: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.cd_crate),
            contentScale = ContentScale.Fit,
            alpha = 0.35f,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer { translationY = hover }
        )
        Text(
            text = stringResource(R.string.rewards_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Accent
        )
        Text(
            text = stringResource(R.string.rewards_empty_body),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
