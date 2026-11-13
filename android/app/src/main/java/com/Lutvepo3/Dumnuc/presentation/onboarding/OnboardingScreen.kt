package com.Lutvepo3.Dumnuc.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.Danger
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.GlyphIcon
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.QuietButton
import com.Lutvepo3.Dumnuc.core.ui.ScreenBackground
import com.Lutvepo3.Dumnuc.core.ui.loopFloat

private val OnboardingOverlay = listOf(
    Color(0xE6101827),
    Color(0xCC16223A),
    Color(0xF2101827)
)

private data class Lesson(
    val glyph: Glyph,
    val accent: Color,
    val titleRes: Int,
    val bodyRes: Int,
    val markRes: Int,
    val spriteRes: Int,
    val elevation: Dp
)

private val Lessons = listOf(
    Lesson(
        Glyph.PLANE, Secondary, R.string.lesson_1_title, R.string.lesson_1_body,
        R.string.lesson_1_mark, R.drawable.sprite_1, 10.dp
    ),
    Lesson(
        Glyph.RING, Accent, R.string.lesson_2_title, R.string.lesson_2_body,
        R.string.lesson_2_mark, R.drawable.sprite_2, 14.dp
    ),
    Lesson(
        Glyph.WARNING, Danger, R.string.lesson_3_title, R.string.lesson_3_body,
        R.string.lesson_3_mark, R.drawable.sprite_3, 18.dp
    )
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val onboardingViewModel: OnboardingViewModel = viewModel(factory = ServiceLocator.factory())
    val state by onboardingViewModel.state.collectAsStateWithLifecycle()

    OnboardingContent(
        state = state,
        onSelect = onboardingViewModel::select,
        onStart = {
            onboardingViewModel.markSeen()
            onDone()
        },
        onSkip = {
            onboardingViewModel.markSeen()
            onDone()
        }
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    onSelect: (Int) -> Unit,
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "onboardingDecor")
    val hover by transition.loopFloat(-5f, 5f, 2400, label = "hover")
    val live = if (state.reducedMotion) 0f else hover

    ScreenBackground(overlay = OnboardingOverlay, imageAlpha = 0.5f) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Lessons.forEachIndexed { index, lesson ->
                    LessonCard(
                        lesson = lesson,
                        expanded = state.selected == index,
                        hover = if (state.selected == index) live else 0f,
                        onClick = { onSelect(index) }
                    )
                }
            }

            PageDots(selected = state.selected, count = Lessons.size)

            Text(
                text = stringResource(R.string.fictional_note),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )

            PrimaryButton(
                label = stringResource(R.string.start_flying),
                onClick = onStart,
                glyph = Glyph.PLAY
            )

            QuietButton(
                label = stringResource(R.string.skip),
                onClick = onSkip,
                accent = TextMuted,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LessonCard(
    lesson: Lesson,
    expanded: Boolean,
    hover: Float,
    onClick: () -> Unit
) {
    val borderAccent by animateColorAsState(
        targetValue = if (expanded) lesson.accent else lesson.accent.copy(alpha = 0.5f),
        animationSpec = tween(220),
        label = "lessonAccent"
    )
    GlassCard(
        accent = borderAccent,
        elevation = lesson.elevation,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(lesson.accent.copy(alpha = 0.14f))
            ) {
                GlyphIcon(glyph = lesson.glyph, tint = lesson.accent, size = 28.dp)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(lesson.titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(lesson.markRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = lesson.accent.copy(alpha = 0.85f)
                )
            }
            Image(
                painter = painterResource(lesson.spriteRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer { translationY = hover }
            )
        }
        if (expanded) {
            Text(
                text = stringResource(lesson.bodyRes),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PageDots(selected: Int, count: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (index in 0 until count) {
            val active = index == selected
            val width by animateDpAsState(
                targetValue = if (active) 20.dp else 8.dp,
                animationSpec = tween(220),
                label = "dotWidth"
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (active) Secondary else SurfaceAlt)
            )
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}
