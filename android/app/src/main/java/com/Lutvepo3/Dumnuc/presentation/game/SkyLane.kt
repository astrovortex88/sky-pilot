package com.Lutvepo3.Dumnuc.presentation.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.config.GameConfig
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.Danger
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceCard
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

private const val PLANE_SIZE = 72
private const val RING_SIZE = 54
private const val STORM_SIZE = 92

@Composable
fun SkyLane(
    state: GameUiState,
    onTap: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val mirrored = state.controlSideLeft
    val stars = remember {
        val random = Random(7919)
        List(42) { Triple(random.nextFloat(), random.nextFloat(), random.nextFloat()) }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(mirrored) {
                detectTapGestures { offset ->
                    val leftHalf = offset.x < size.width / 2f
                    onTap(if (mirrored) !leftHalf else leftHalf)
                }
            }
    ) {
        val laneWidth = maxWidth
        val laneHeight = maxHeight
        val scroll = state.elapsedRaw

        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                val speed = 0.09f + star.third * 0.2f
                val y = ((star.second + scroll * speed) % 1f) * size.height
                drawCircle(
                    color = TextPrimary.copy(alpha = 0.2f + star.third * 0.25f),
                    radius = (1.1f + star.third * 1.4f).dp.toPx(),
                    center = Offset(star.first * size.width, y)
                )
            }
            for (index in 0 until 3) {
                val cloudY = (((index * 0.37f) + scroll * 0.05f) % 1.2f - 0.1f) * size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = 90.dp.toPx(),
                    center = Offset(size.width * (0.2f + index * 0.3f), cloudY)
                )
            }
            for (lane in 0 until GameConfig.LANE_COUNT) {
                val active = abs(state.lanePos - lane) < 0.5f
                val x = size.width * laneFraction(lane)
                drawLine(
                    color = if (active) {
                        Secondary.copy(alpha = 0.3f)
                    } else {
                        Color.White.copy(alpha = 0.08f)
                    },
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = if (active) 2.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            val planeX = size.width * laneFraction(state.lanePos)
            val planeY = size.height * GameConfig.PLANE_ROW_FRACTION
            for (index in 1..6) {
                drawCircle(
                    color = Secondary.copy(alpha = 0.3f - index * 0.04f),
                    radius = (6f - index * 0.6f).dp.toPx(),
                    center = Offset(
                        planeX - index * 7.dp.toPx(),
                        planeY + 14.dp.toPx() + sin(scroll * 6f + index) * 2.dp.toPx()
                    )
                )
            }
            if (state.ringPulse > 0f) {
                drawCircle(
                    color = Secondary.copy(alpha = 0.35f * state.ringPulse),
                    radius = (1f - state.ringPulse) * 120.dp.toPx() + 10.dp.toPx(),
                    center = Offset(planeX, planeY)
                )
            }
        }

        state.entities.forEach { entity ->
            val sizeDp = if (entity.ring) RING_SIZE.dp else STORM_SIZE.dp
            val pulse = if (entity.ring && !state.reducedMotion) {
                0.94f + 0.12f * ((sin(scroll * 7f + entity.id) + 1f) / 2f)
            } else {
                1f
            }
            Image(
                painter = painterResource(
                    if (entity.ring) R.drawable.sprite_2 else R.drawable.sprite_3
                ),
                contentDescription = stringResource(
                    if (entity.ring) R.string.cd_ring else R.string.cd_storm
                ),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(sizeDp)
                    .offset(
                        x = laneWidth * laneFraction(entity.lane.toFloat()) - sizeDp / 2,
                        y = laneHeight * entity.y - sizeDp / 2
                    )
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                        alpha = if (entity.warning) 0.85f else 1f
                    }
            )
            if (entity.warning) {
                Box(
                    modifier = Modifier
                        .size(sizeDp)
                        .offset(
                            x = laneWidth * laneFraction(entity.lane.toFloat()) - sizeDp / 2,
                            y = laneHeight * entity.y - sizeDp / 2
                        )
                        .clip(RoundedCornerShape(sizeDp / 2))
                        .background(Danger.copy(alpha = 0.22f))
                )
            }
        }

        val planeAlpha = if (state.recovering) 0.45f else 1f
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_plane),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color(state.liveryColor), BlendMode.Modulate),
            modifier = Modifier
                .size(PLANE_SIZE.dp)
                .offset(
                    x = laneWidth * laneFraction(state.lanePos) - PLANE_SIZE.dp / 2,
                    y = laneHeight * GameConfig.PLANE_ROW_FRACTION - PLANE_SIZE.dp / 2
                )
                .graphicsLayer {
                    rotationZ = state.bankDegrees
                    alpha = planeAlpha
                    val boost = if (state.boosting) 1.08f else 1f
                    scaleX = boost
                    scaleY = boost
                }
        )

        if (state.goldFlash > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Accent.copy(alpha = 0.22f * state.goldFlash))
            )
            Text(
                text = stringResource(R.string.boost),
                style = MaterialTheme.typography.displaySmall,
                color = Accent,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        val grow = 0.8f + 0.4f * state.goldFlash
                        scaleX = grow
                        scaleY = grow
                        alpha = state.goldFlash
                    }
            )
        }

        if (state.dangerFlash > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.Transparent,
                                Danger.copy(alpha = 0.35f * state.dangerFlash)
                            )
                        )
                    )
            )
        }

        if (state.recovering) {
            Text(
                text = stringResource(R.string.recovered),
                style = MaterialTheme.typography.titleMedium,
                color = Secondary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 120.dp)
            )
        }

        if (state.hintVisible) {
            Text(
                text = stringResource(
                    if (mirrored) R.string.control_hint_left else R.string.control_hint
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 34.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard.copy(alpha = 0.78f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

private fun laneFraction(lane: Float): Float = 0.22f + lane * 0.28f

private fun laneFraction(lane: Int): Float = laneFraction(lane.toFloat())
