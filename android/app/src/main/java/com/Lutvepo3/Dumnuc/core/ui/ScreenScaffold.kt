package com.Lutvepo3.Dumnuc.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.Lutvepo3.Dumnuc.core.theme.Secondary

private val HubOverlay = listOf(
    Color(0xE6101827),
    Color(0xB316223A),
    Color(0xF2101827)
)

@Composable
fun ScreenScaffold(
    title: String,
    subtitle: String,
    menuLabel: String,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Secondary,
    content: @Composable ColumnScope.() -> Unit
) {
    ScreenBackground(overlay = HubOverlay, imageAlpha = 0.55f, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = accent.copy(alpha = 0.85f)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                content = content
            )
            HomeButton(
                label = menuLabel,
                onClick = onMenu,
                accent = accent
            )
        }
    }
}
