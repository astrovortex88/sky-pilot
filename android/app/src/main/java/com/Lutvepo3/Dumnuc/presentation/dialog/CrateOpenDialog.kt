package com.Lutvepo3.Dumnuc.presentation.dialog

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.loopFloat
import com.Lutvepo3.Dumnuc.core.theme.GoldGradient
import com.Lutvepo3.Dumnuc.core.theme.BgDeep

@Composable
fun CrateOpenDialog(
    points: Int,
    itemNameRes: Int?,
    reducedMotion: Boolean,
    onCollect: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "crateReveal")
    val pulse by transition.loopFloat(1f, 1.15f, 520, label = "cratePulse")
    val scale = if (reducedMotion) 1f else pulse

    Dialog(onDismissRequest = onCollect) {
        GlassCard(
            accent = Accent,
            elevation = 24.dp,
            radius = 24.dp,
            padding = 20.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.crate_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Accent
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = stringResource(R.string.cd_crate),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
            Text(
                text = stringResource(R.string.crate_points, points),
                style = MaterialTheme.typography.titleLarge,
                color = Accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (itemNameRes != null) {
                Text(
                    text = stringResource(R.string.unlocked_item, stringResource(itemNameRes)),
                    style = MaterialTheme.typography.labelMedium,
                    color = Secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            PrimaryButton(
                label = stringResource(R.string.collect),
                onClick = onCollect,
                glyph = Glyph.CHECK,
                colors = GoldGradient,
                contentColor = BgDeep,
                height = 56.dp
            )
        }
    }
}
