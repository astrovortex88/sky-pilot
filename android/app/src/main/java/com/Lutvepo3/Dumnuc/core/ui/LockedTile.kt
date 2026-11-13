package com.Lutvepo3.Dumnuc.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Lutvepo3.Dumnuc.core.theme.TextMuted

@Composable
fun LockedTile(
    spriteRes: Int,
    modifier: Modifier = Modifier,
    spriteSize: Dp = 56.dp,
    lockTint: Color = TextMuted
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(spriteSize)
    ) {
        Image(
            painter = painterResource(spriteRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alpha = 0.18f,
            modifier = Modifier.size(spriteSize)
        )
        GlyphIcon(glyph = Glyph.LOCK, tint = lockTint, size = spriteSize * 0.42f)
    }
}
