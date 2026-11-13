package com.Lutvepo3.Dumnuc.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.ScreenBackground
import com.Lutvepo3.Dumnuc.core.ui.loopFloat
import kotlin.math.sin
import kotlin.random.Random

private val SplashOverlay = listOf(
    Color(0xFF090F1C),
    Color(0xF00D1526),
    Color(0xFA101827)
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.factory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    val reducedMotion by splashViewModel.reducedMotion.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent(reducedMotion = reducedMotion)
}

@Composable
private fun SplashContent(reducedMotion: Boolean) {
    val transition = rememberInfiniteTransition(label = "splashDecor")
    val sweep by transition.loopFloat(0f, 360f, 4200, LinearEasing, RepeatMode.Restart, "sweep")
    val twinkle by transition.loopFloat(0.15f, 0.75f, 2600, FastOutSlowInEasing, label = "twinkle")
    val drift by transition.loopFloat(-40f, 40f, 6000, FastOutSlowInEasing, label = "drift")
    val breathe by transition.loopFloat(1f, 1.03f, 2600, FastOutSlowInEasing, label = "breathe")

    val liveSweep = if (reducedMotion) 36f else sweep
    val liveTwinkle = if (reducedMotion) 0.5f else twinkle
    val liveDrift = if (reducedMotion) 0f else drift
    val liveBreathe = if (reducedMotion) 1f else breathe

    val stars = remember {
        val random = Random(4201)
        List(26) {
            Triple(random.nextFloat(), random.nextFloat(), 0.4f + random.nextFloat() * 0.6f)
        }
    }

    ScreenBackground(overlay = SplashOverlay, imageAlpha = 0.35f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                val alpha = (liveTwinkle * star.third).coerceIn(0.08f, 0.85f)
                drawCircle(
                    color = TextPrimary.copy(alpha = alpha),
                    radius = 1.2f.dp.toPx() + star.third * 1.4f.dp.toPx(),
                    center = Offset(star.first * size.width, star.second * size.height * 0.92f)
                )
            }
            val radius = 148.dp.toPx()
            val center = Offset(size.width / 2f, size.height * 0.42f)
            drawArc(
                color = Secondary.copy(alpha = 0.22f),
                startAngle = liveSweep,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Secondary.copy(alpha = 0.08f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 1.dp.toPx())
            )
            val bandY = size.height * 0.78f
            for (index in 0 until 5) {
                val offsetX = liveDrift.dp.toPx() + index * size.width / 5f
                drawCircle(
                    color = Secondary.copy(alpha = 0.05f),
                    radius = 70.dp.toPx(),
                    center = Offset(
                        offsetX % (size.width + 140.dp.toPx()) - 70.dp.toPx(),
                        bandY + sin(index.toFloat()) * 22.dp.toPx()
                    )
                )
            }
            val glowRadius = 230.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Secondary.copy(alpha = 0.14f), Color.Transparent),
                    center = center,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer {
                        scaleX = liveBreathe
                        scaleY = liveBreathe
                    }
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Secondary,
                        spotColor = Secondary
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF1C2B47).copy(alpha = 0.55f))
                    .border(1.dp, Secondary.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(136.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelLarge,
                color = Secondary.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                color = Secondary,
                trackColor = SurfaceAlt,
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
