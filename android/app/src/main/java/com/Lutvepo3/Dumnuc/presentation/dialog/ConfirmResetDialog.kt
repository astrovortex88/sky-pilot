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
import com.Lutvepo3.Dumnuc.core.theme.Danger
import com.Lutvepo3.Dumnuc.core.theme.DangerGradient
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.PrimaryButton
import com.Lutvepo3.Dumnuc.core.ui.SecondaryButton

@Composable
fun ConfirmResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            accent = Danger,
            elevation = 22.dp,
            radius = 24.dp,
            padding = 20.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.reset_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Danger
            )
            Text(
                text = stringResource(R.string.reset_dialog_body),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PrimaryButton(
                label = stringResource(R.string.confirm_reset),
                onClick = onConfirm,
                glyph = Glyph.WARNING,
                colors = DangerGradient,
                contentColor = TextPrimary,
                height = 56.dp
            )
            SecondaryButton(
                label = stringResource(R.string.cancel),
                onClick = onDismiss,
                glyph = Glyph.CHECK
            )
        }
    }
}
