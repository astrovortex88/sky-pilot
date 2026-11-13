package com.Lutvepo3.Dumnuc.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Lutvepo3.Dumnuc.R
import com.Lutvepo3.Dumnuc.core.di.ServiceLocator
import com.Lutvepo3.Dumnuc.core.theme.Accent
import com.Lutvepo3.Dumnuc.core.theme.BgDeep
import com.Lutvepo3.Dumnuc.core.theme.Danger
import com.Lutvepo3.Dumnuc.core.theme.Secondary
import com.Lutvepo3.Dumnuc.core.theme.SurfaceAlt
import com.Lutvepo3.Dumnuc.core.theme.TextMuted
import com.Lutvepo3.Dumnuc.core.theme.TextPrimary
import com.Lutvepo3.Dumnuc.core.ui.GlassCard
import com.Lutvepo3.Dumnuc.core.ui.Glyph
import com.Lutvepo3.Dumnuc.core.ui.GlyphIcon
import com.Lutvepo3.Dumnuc.core.ui.ScreenScaffold
import com.Lutvepo3.Dumnuc.core.ui.SecondaryButton
import com.Lutvepo3.Dumnuc.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsScreen(onMenu: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory())
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
    }

    SettingsContent(
        state = state,
        onControlSide = settingsViewModel::setControlSideLeft,
        onAssist = settingsViewModel::setAssistMode,
        onAutoCashOut = settingsViewModel::setAutoCashOut,
        onReducedMotion = settingsViewModel::setReducedMotion,
        onAskReset = settingsViewModel::askReset,
        onDismissReset = settingsViewModel::dismissReset,
        onConfirmReset = settingsViewModel::confirmReset,
        onMenu = onMenu
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onControlSide: (Boolean) -> Unit,
    onAssist: (Boolean) -> Unit,
    onAutoCashOut: (Float) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onAskReset: () -> Unit,
    onDismissReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenScaffold(
        title = stringResource(R.string.settings_title),
        subtitle = stringResource(R.string.settings_subtitle),
        menuLabel = stringResource(R.string.menu),
        onMenu = onMenu,
        accent = Secondary
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                SettingRow(
                    glyph = Glyph.PLANE,
                    titleRes = R.string.setting_control_side,
                    hintRes = R.string.setting_control_side_hint,
                    accent = Secondary,
                    status = stringResource(
                        if (state.controlSideLeft) R.string.side_left else R.string.side_right
                    )
                ) {
                    SegmentedPair(
                        leftLabel = stringResource(R.string.side_right),
                        rightLabel = stringResource(R.string.side_left),
                        leftSelected = !state.controlSideLeft,
                        onLeft = { onControlSide(false) },
                        onRight = { onControlSide(true) }
                    )
                }
            }
            item {
                SettingRow(
                    glyph = Glyph.WARNING,
                    titleRes = R.string.setting_assist,
                    hintRes = R.string.setting_assist_hint,
                    accent = Accent,
                    status = stringResource(
                        if (state.assistMode) R.string.state_on else R.string.state_off
                    )
                ) {
                    Switch(
                        checked = state.assistMode,
                        onCheckedChange = onAssist,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgDeep,
                            checkedTrackColor = Accent,
                            uncheckedTrackColor = SurfaceAlt
                        )
                    )
                }
            }
            item {
                SettingRow(
                    glyph = Glyph.TARGET,
                    titleRes = R.string.setting_auto_cash,
                    hintRes = R.string.setting_auto_cash_hint,
                    accent = Accent,
                    status = autoLabel(state.autoCashOut)
                ) {
                    AutoCashRow(selected = state.autoCashOut, onSelect = onAutoCashOut)
                }
            }
            item {
                SettingRow(
                    glyph = Glyph.STAR,
                    titleRes = R.string.setting_reduced_motion,
                    hintRes = R.string.setting_reduced_motion_hint,
                    accent = Secondary,
                    status = stringResource(
                        if (state.reducedMotion) R.string.state_on else R.string.state_off
                    )
                ) {
                    Switch(
                        checked = state.reducedMotion,
                        onCheckedChange = onReducedMotion,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgDeep,
                            checkedTrackColor = Secondary,
                            uncheckedTrackColor = SurfaceAlt
                        )
                    )
                }
            }
            item {
                SettingRow(
                    glyph = Glyph.REFRESH,
                    titleRes = R.string.setting_reset,
                    hintRes = R.string.setting_reset_hint,
                    accent = Danger,
                    status = stringResource(R.string.reset)
                ) {
                    SecondaryButton(
                        label = stringResource(R.string.reset),
                        onClick = onAskReset,
                        glyph = Glyph.WARNING,
                        accent = Danger
                    )
                }
            }
            item {
                Text(
                    text = stringResource(R.string.fictional_note),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }

    if (state.showResetDialog) {
        ConfirmResetDialog(onConfirm = onConfirmReset, onDismiss = onDismissReset)
    }
}

@Composable
private fun autoLabel(value: Float): String = when {
    value >= 5f -> stringResource(R.string.auto_five)
    value >= 3f -> stringResource(R.string.auto_three)
    value >= 2f -> stringResource(R.string.auto_two)
    else -> stringResource(R.string.auto_off)
}

@Composable
private fun SettingRow(
    glyph: Glyph,
    titleRes: Int,
    hintRes: Int,
    accent: Color,
    status: String,
    control: @Composable () -> Unit
) {
    GlassCard(
        accent = accent,
        elevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = status }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.16f))
            ) {
                GlyphIcon(glyph = glyph, tint = accent, size = 22.dp)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(hintRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            control()
        }
    }
}

@Composable
private fun SegmentedPair(
    leftLabel: String,
    rightLabel: String,
    leftSelected: Boolean,
    onLeft: () -> Unit,
    onRight: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Segment(label = leftLabel, selected = leftSelected, onClick = onLeft)
        Segment(label = rightLabel, selected = !leftSelected, onClick = onRight)
    }
}

@Composable
private fun AutoCashRow(selected: Float, onSelect: (Float) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Segment(
            label = stringResource(R.string.auto_off),
            selected = selected < 2f,
            onClick = { onSelect(0f) }
        )
        Segment(
            label = stringResource(R.string.auto_two),
            selected = selected >= 2f && selected < 3f,
            onClick = { onSelect(2f) }
        )
        Segment(
            label = stringResource(R.string.auto_three),
            selected = selected >= 3f && selected < 5f,
            onClick = { onSelect(3f) }
        )
        Segment(
            label = stringResource(R.string.auto_five),
            selected = selected >= 5f,
            onClick = { onSelect(5f) }
        )
    }
}

@Composable
private fun Segment(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(if (selected) Secondary.copy(alpha = 0.22f) else SurfaceAlt.copy(alpha = 0.7f))
            .border(1.dp, if (selected) Secondary else TextMuted.copy(alpha = 0.3f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Secondary else TextMuted,
            maxLines = 1
        )
    }
}
