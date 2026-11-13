package com.Lutvepo3.Dumnuc.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Lutvepo3.Dumnuc.core.theme.PrimaryGradient
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary

@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyph: Glyph? = Glyph.PLAY,
    colors: List<Color> = PrimaryGradient,
    contentColor: Color = TextPrimary,
    enabled: Boolean = true,
    height: Dp = 60.dp,
    status: String? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "primaryPress"
    )
    val shape = RoundedCornerShape(18.dp)
    val paint = if (enabled) colors else colors.map { it.copy(alpha = 0.45f) }
    val statusModifier = if (status == null) {
        Modifier
    } else {
        Modifier.semantics { stateDescription = status }
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interaction,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled) 16.dp else 0.dp,
                shape = shape,
                ambientColor = paint.first(),
                spotColor = paint.first()
            )
            .background(Brush.horizontalGradient(paint), shape)
            .then(statusModifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (glyph != null) {
                GlyphIcon(glyph = glyph, tint = contentColor, size = 24.dp)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyph: Glyph? = null,
    accent: Color = Secondary,
    height: Dp = 48.dp
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "secondaryPress"
    )
    val shape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        shape = shape,
        interactionSource = interaction,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceAlt.copy(alpha = 0.78f),
            contentColor = accent
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height)
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(1.dp, accent.copy(alpha = 0.4f), shape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (glyph != null) {
                GlyphIcon(glyph = glyph, tint = accent, size = 22.dp)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = accent,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuietButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyph: Glyph? = null,
    accent: Color = Secondary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (glyph != null) {
                GlyphIcon(glyph = glyph, tint = accent, size = 20.dp)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = accent
            )
        }
    }
}
