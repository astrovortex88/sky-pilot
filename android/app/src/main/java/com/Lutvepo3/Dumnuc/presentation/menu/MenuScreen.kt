package com.Lutvepo3.Dumnuc.presentation.menu

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.navigation.Screen
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.PrimaryGradient
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.SurfaceCard
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.theme.TextSecondary
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.GlyphIcon
import com.Lutvepo3.Dumnuc.core.ui.QuietButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenBackground
import com.Lutvepo3.Dumnuc.core.ui.SectionProgressBar
import com.Lutvepo3.Dumnuc.core.ui.loopFloat

private val MenuOverlay = listOf(
    Color(0xB3101827),
    Color(0x8016223A),
    Color(0xF2101827)
)

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onOpen: (Screen) -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = { onPlay(state.stageIndex) },
        onOpen = onOpen
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onOpen: (Screen) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menuDecor")
    val cloudA by transition.loopFloat(-18f, 18f, 9000, label = "cloudA")
    val cloudB by transition.loopFloat(18f, -18f, 13000, label = "cloudB")
    val planeHover by transition.loopFloat(-6f, 6f, 1800, label = "planeHover")
    val still = state.reducedMotion
    val driftA = if (still) 0f else cloudA
    val driftB = if (still) 0f else cloudB
    val hover = if (still) 0f else planeHover

    ScreenBackground(overlay = MenuOverlay, imageAlpha = 0.62f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val glowRadius = 260.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Secondary.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.34f),
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = Offset(size.width * 0.5f, size.height * 0.34f)
            )
            for (index in 0 until 4) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    radius = 56.dp.toPx(),
                    center = Offset(
                        size.width * (0.15f + index * 0.25f) + driftA.dp.toPx(),
                        size.height * 0.2f
                    )
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = 86.dp.toPx(),
                    center = Offset(
                        size.width * (0.3f + index * 0.22f) + driftB.dp.toPx(),
                        size.height * 0.62f
                    )
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            MenuHeader(points = state.points)
            RankStrip(state = state)
            HeroTile(state = state, hover = hover, onPlay = onPlay)
            BentoGrid(
                state = state,
                onOpen = onOpen,
                modifier = Modifier.weight(1f)
            )
            QuietButton(
                label = stringResource(R.string.tutorial),
                onClick = { onOpen(Screen.Onboarding) },
                glyph = Glyph.INFO,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MenuHeader(points: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = stringResource(R.string.brand),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        val label = stringResource(R.string.sp_value, points)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .height(38.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(SurfaceAlt.copy(alpha = 0.85f))
                .border(1.dp, Accent.copy(alpha = 0.45f), RoundedCornerShape(19.dp))
                .padding(horizontal = 12.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                }
        ) {
            GlyphIcon(glyph = Glyph.STAR, tint = Accent, size = 16.dp)
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = Accent,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RankStrip(state: MenuUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(
                R.string.rank_strip,
                stringResource(state.rankLabelRes),
                state.stageIndex,
                state.totalStages
            ),
            style = MaterialTheme.typography.labelMedium
        )
        SectionProgressBar(
            progress = state.trackFraction,
            accent = Secondary,
            barHeight = 4.dp
        )
    }
}

@Composable
private fun HeroTile(state: MenuUiState, hover: Float, onPlay: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "heroPress"
    )
    val shape = RoundedCornerShape(24.dp)

    Button(
        onClick = onPlay,
        shape = shape,
        interactionSource = interaction,
        elevation = null,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(20.dp, shape, ambientColor = Secondary, spotColor = Secondary)
            .background(Brush.linearGradient(PrimaryGradient), shape)
            .border(1.dp, TextPrimary.copy(alpha = 0.18f), shape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(
                        R.string.hero_stage,
                        state.stageIndex,
                        stringResource(state.stageNameRes)
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary.copy(alpha = 0.85f)
                )
                Text(
                    text = stringResource(R.string.hero_target, state.stageTarget),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlyphIcon(glyph = Glyph.PLAY, tint = TextPrimary, size = 26.dp)
                    Text(
                        text = stringResource(R.string.play),
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                        color = TextPrimary
                    )
                }
            }
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_plane),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(86.dp)
                    .graphicsLayer {
                        rotationZ = -12f
                        translationY = hover
                    }
            )
        }
    }
}

@Composable
private fun BentoGrid(
    state: MenuUiState,
    onOpen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        state.tiles.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                row.forEach { tile ->
                    DestinationTile(
                        tile = tile,
                        onOpen = onOpen,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationTile(
    tile: MenuTile,
    onOpen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = Color(tile.accent)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "tilePress"
    )
    val shape = RoundedCornerShape(18.dp)
    val label = stringResource(tile.labelRes)
    val value = stringResource(tile.value.res, *tile.value.args.toTypedArray())

    Button(
        onClick = { onOpen(tile.target) },
        shape = shape,
        interactionSource = interaction,
        elevation = null,
        contentPadding = PaddingValues(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceCard.copy(alpha = 0.8f),
            contentColor = TextPrimary
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(8.dp, shape, ambientColor = accent, spotColor = accent)
            .border(1.dp, accent.copy(alpha = 0.38f), shape)
            .semantics { stateDescription = value }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            GlyphIcon(glyph = tile.glyph, tint = accent, size = 22.dp)
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
