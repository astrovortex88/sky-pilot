package com.Lutvepo3.Dumnuc.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.Lutvepo3.Dumnuc.core.theme.SurfaceCard
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.LockedTile
import com.Lutvepo3.Dumnuc.core.ui.ScreenScaffold
import com.Lutvepo3.Dumnuc.core.ui.SectionProgressBar

@Composable
fun CollectionScreen(onMenu: () -> Unit) {
    val collectionViewModel: CollectionViewModel = viewModel(factory = ServiceLocator.factory())
    val state by collectionViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        collectionViewModel.refresh()
    }

    CollectionContent(
        state = state,
        onEquip = collectionViewModel::equip,
        onMenu = onMenu
    )
}

@Composable
private fun CollectionContent(
    state: CollectionUiState,
    onEquip: (String) -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        title = stringResource(R.string.collection_title),
        subtitle = stringResource(
            R.string.collection_subtitle,
            state.unlocked,
            state.total
        ),
        menuLabel = stringResource(R.string.menu),
        onMenu = onMenu,
        accent = Secondary
    ) {
        SectionProgressBar(
            progress = if (state.total == 0) 0f else state.unlocked.toFloat() / state.total,
            accent = Secondary
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.section_liveries),
                    style = MaterialTheme.typography.labelMedium,
                    color = Secondary
                )
            }
            items(state.liveries, key = { cell -> cell.id }) { cell ->
                CollectionTile(cell = cell, onEquip = onEquip)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.section_badges),
                    style = MaterialTheme.typography.labelMedium,
                    color = Accent
                )
            }
            items(state.badges, key = { cell -> cell.id }) { cell ->
                CollectionTile(cell = cell, onEquip = onEquip)
            }
        }
    }
}

@Composable
private fun CollectionTile(cell: CollectionCell, onEquip: (String) -> Unit) {
    val accent = Color(cell.colorArgb)
    val shape = RoundedCornerShape(16.dp)
    val sprite = if (cell.livery) R.drawable.sprite_1 else R.drawable.sprite_5
    val status = if (cell.equipped) {
        stringResource(R.string.equipped)
    } else if (cell.unlocked) {
        stringResource(R.string.equip)
    } else {
        stringResource(cell.unlockTextRes, cell.unlockValue)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(shape)
            .background(SurfaceCard.copy(alpha = 0.76f), shape)
            .border(
                if (cell.equipped) 2.dp else 1.dp,
                accent.copy(alpha = if (cell.unlocked) 0.7f else 0.25f),
                shape
            )
            .clickable(enabled = cell.unlocked) { onEquip(cell.id) }
            .padding(10.dp)
            .semantics { stateDescription = status }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
            if (cell.unlocked) {
                Image(
                    painter = painterResource(sprite),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(accent, BlendMode.Modulate),
                    modifier = Modifier.size(56.dp)
                )
            } else {
                LockedTile(spriteRes = sprite, spriteSize = 56.dp)
            }
        }
        Text(
            text = stringResource(cell.nameRes),
            style = MaterialTheme.typography.labelSmall,
            color = if (cell.unlocked) TextPrimary else TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = if (cell.equipped) Accent else TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
