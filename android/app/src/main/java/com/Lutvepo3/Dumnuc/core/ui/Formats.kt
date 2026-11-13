package com.Lutvepo3.Dumnuc.core.ui

import java.util.Locale
import kotlin.math.ceil

object Formats {

    fun points(value: Int): String = String.format(Locale.US, "%,d", value)

    fun multiplier(value: Float): String = String.format(Locale.US, "%.2f", value)

    fun factor(value: Float): String = String.format(Locale.US, "%.1f", value)

    fun seconds(value: Float): Int = ceil(value.coerceAtLeast(0f)).toInt()
}
