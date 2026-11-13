package com.Lutvepo3.Dumnuc.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.theme.BgDeep
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceCard

val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun ProvideMotionPreference(reducedMotion: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalReducedMotion provides reducedMotion, content = content)
}

@Composable
fun ScreenBackground(
    overlay: List<Color>,
    imageAlpha: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(BgDeep)) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = imageAlpha,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accent: Color = Secondary,
    elevation: Dp = 12.dp,
    radius: Dp = 20.dp,
    fill: Color = SurfaceCard.copy(alpha = 0.78f),
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    val clickModifier = if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(onClick = onClick)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .shadow(elevation, shape, ambientColor = accent, spotColor = accent)
            .clip(shape)
            .background(fill, shape)
            .border(1.dp, accent.copy(alpha = 0.38f), shape)
            .then(clickModifier)
            .padding(padding)
            .fillMaxWidth(),
        content = content
    )
}

@Composable
fun SectionProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    accent: Color = Secondary,
    track: Color = Color.White.copy(alpha = 0.12f),
    barHeight: Dp = 6.dp
) {
    val shape = RoundedCornerShape(barHeight / 2)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(shape)
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(shape)
                .background(
                    Brush.horizontalGradient(listOf(accent.copy(alpha = 0.6f), accent))
                )
        )
    }
}
