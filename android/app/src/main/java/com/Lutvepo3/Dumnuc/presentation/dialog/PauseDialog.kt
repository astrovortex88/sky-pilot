package com.Lutvepo3.Dumnuc.presentation.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.HomeButton
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        GlassCard(
            accent = Secondary,
            elevation = 22.dp,
            radius = 24.dp,
            padding = 20.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.pause_body),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PrimaryButton(
                label = stringResource(R.string.resume),
                onClick = onResume,
                glyph = Glyph.PLAY,
                height = 56.dp
            )
            SecondaryButton(
                label = stringResource(R.string.restart),
                onClick = onRestart,
                glyph = Glyph.REFRESH
            )
            HomeButton(
                label = stringResource(R.string.menu),
                onClick = onMenu
            )
        }
    }
}
