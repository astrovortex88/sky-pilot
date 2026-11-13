package com.Lutvepo3.Dumnuc.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

enum class Glyph {
    PLAY,
    STAR,
    WARNING,
    CHECK,
    LOCK,
    INFO,
    MAP,
    CALENDAR,
    HEART,
    CRATE,
    PERSON,
    GEAR,
    REFRESH,
    BOLT,
    RING,
    PLANE,
    FUEL,
    TARGET,
    CLOCK,
    TROPHY
}

@Composable
fun GlyphIcon(
    glyph: Glyph,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        drawGlyph(glyph, tint)
    }
}

private const val DEG_TO_RAD = 0.017453292f

private fun DrawScope.drawGlyph(glyph: Glyph, tint: Color) {
    val unit = size.minDimension / 24f
    val stroke = Stroke(width = unit * 2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val thin = Stroke(width = unit * 1.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val mid = unit * 12f
    val hub = Offset(mid, mid)

    when (glyph) {
        Glyph.PLAY -> drawPath(polygon(unit, 7f, 4.5f, 19.5f, 12f, 7f, 19.5f), tint)

        Glyph.STAR -> drawPath(starPath(unit, 5, 10f, 4.4f), tint)

        Glyph.TROPHY -> {
            drawPath(starPath(unit, 5, 7.2f, 3.1f), tint)
            drawLine(tint, at(unit, 12f, 17f), at(unit, 12f, 20f), unit * 2f, StrokeCap.Round)
            drawLine(tint, at(unit, 8f, 21f), at(unit, 16f, 21f), unit * 2f, StrokeCap.Round)
        }

        Glyph.WARNING -> {
            drawPath(polygon(unit, 12f, 3f, 22f, 20.5f, 2f, 20.5f), tint, style = stroke)
            drawLine(tint, at(unit, 12f, 10f), at(unit, 12f, 15f), unit * 2f, StrokeCap.Round)
            drawCircle(tint, unit * 1.1f, at(unit, 12f, 18f))
        }

        Glyph.CHECK -> {
            val path = Path()
            path.moveTo(unit * 4f, unit * 13f)
            path.lineTo(unit * 9.5f, unit * 18.5f)
            path.lineTo(unit * 20f, unit * 6f)
            drawPath(path, tint, style = stroke)
        }

        Glyph.LOCK -> {
            drawRoundRect(
                color = tint,
                topLeft = at(unit, 5f, 11f),
                size = Size(unit * 14f, unit * 10f),
                cornerRadius = CornerRadius(unit * 2.5f, unit * 2.5f)
            )
            val arc = Path()
            arc.arcTo(
                rect = Rect(at(unit, 7.5f, 4.5f), Size(unit * 9f, unit * 9f)),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = true
            )
            drawPath(arc, tint, style = stroke)
        }

        Glyph.INFO -> {
            drawCircle(tint, unit * 9.5f, hub, style = stroke)
            drawCircle(tint, unit * 1.2f, at(unit, 12f, 7.5f))
            drawLine(tint, at(unit, 12f, 11f), at(unit, 12f, 17f), unit * 2f, StrokeCap.Round)
        }

        Glyph.MAP -> {
            drawCircle(tint, unit * 5.6f, at(unit, 12f, 9.2f), style = stroke)
            drawCircle(tint, unit * 2f, at(unit, 12f, 9.2f))
            drawLine(tint, at(unit, 12f, 14.8f), at(unit, 12f, 21f), unit * 2f, StrokeCap.Round)
        }

        Glyph.CALENDAR -> {
            drawRoundRect(
                color = tint,
                topLeft = at(unit, 3.5f, 5.5f),
                size = Size(unit * 17f, unit * 15f),
                cornerRadius = CornerRadius(unit * 2.5f, unit * 2.5f),
                style = stroke
            )
            drawLine(tint, at(unit, 4f, 10.5f), at(unit, 20f, 10.5f), unit * 1.6f)
            drawLine(tint, at(unit, 8f, 2.8f), at(unit, 8f, 6.5f), unit * 2f, StrokeCap.Round)
            drawLine(tint, at(unit, 16f, 2.8f), at(unit, 16f, 6.5f), unit * 2f, StrokeCap.Round)
        }

        Glyph.HEART -> {
            val path = Path()
            path.moveTo(unit * 12f, unit * 20.5f)
            path.cubicTo(unit * 2f, unit * 13.5f, unit * 4f, unit * 4f, unit * 12f, unit * 8.2f)
            path.cubicTo(unit * 20f, unit * 4f, unit * 22f, unit * 13.5f, unit * 12f, unit * 20.5f)
            path.close()
            drawPath(path, tint)
        }

        Glyph.CRATE -> {
            drawRoundRect(
                color = tint,
                topLeft = at(unit, 3.5f, 7f),
                size = Size(unit * 17f, unit * 13.5f),
                cornerRadius = CornerRadius(unit * 2f, unit * 2f),
                style = stroke
            )
            drawLine(tint, at(unit, 4f, 11.5f), at(unit, 20f, 11.5f), unit * 1.5f)
            drawLine(tint, at(unit, 12f, 11.5f), at(unit, 12f, 20f), unit * 1.5f)
            drawLine(tint, at(unit, 7f, 3f), at(unit, 10f, 7f), unit * 1.5f, StrokeCap.Round)
            drawLine(tint, at(unit, 17f, 3f), at(unit, 14f, 7f), unit * 1.5f, StrokeCap.Round)
        }

        Glyph.PERSON -> {
            drawCircle(tint, unit * 4.2f, at(unit, 12f, 7.6f))
            val path = Path()
            path.arcTo(
                rect = Rect(at(unit, 4f, 13f), Size(unit * 16f, unit * 16f)),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = true
            )
            drawPath(path, tint, style = stroke)
        }

        Glyph.GEAR -> {
            drawCircle(tint, unit * 4.2f, hub, style = stroke)
            for (index in 0 until 8) {
                val angle = index * 45f * DEG_TO_RAD
                drawLine(
                    tint,
                    hub + Offset(cos(angle) * unit * 6.6f, sin(angle) * unit * 6.6f),
                    hub + Offset(cos(angle) * unit * 10f, sin(angle) * unit * 10f),
                    unit * 2f,
                    StrokeCap.Round
                )
            }
        }

        Glyph.REFRESH -> {
            val path = Path()
            path.arcTo(
                rect = Rect(at(unit, 3.5f, 3.5f), Size(unit * 17f, unit * 17f)),
                startAngleDegrees = 50f,
                sweepAngleDegrees = 280f,
                forceMoveTo = true
            )
            drawPath(path, tint, style = stroke)
            drawPath(polygon(unit, 20f, 2.5f, 22f, 10.5f, 14.5f, 8f), tint)
        }

        Glyph.BOLT -> {
            val path = Path()
            path.moveTo(unit * 13.5f, unit * 2f)
            path.lineTo(unit * 5f, unit * 13.5f)
            path.lineTo(unit * 11f, unit * 13.5f)
            path.lineTo(unit * 10f, unit * 22f)
            path.lineTo(unit * 19f, unit * 10f)
            path.lineTo(unit * 12.8f, unit * 10f)
            path.close()
            drawPath(path, tint)
        }

        Glyph.RING -> {
            drawCircle(tint, unit * 9.5f, hub, style = stroke)
            drawCircle(tint, unit * 4.6f, hub, style = thin)
        }

        Glyph.PLANE -> {
            val path = Path()
            path.moveTo(unit * 2f, unit * 14.5f)
            path.lineTo(unit * 21.5f, unit * 7.5f)
            path.lineTo(unit * 19f, unit * 12.5f)
            path.lineTo(unit * 9.5f, unit * 16f)
            path.lineTo(unit * 8f, unit * 21f)
            path.lineTo(unit * 5.5f, unit * 16.5f)
            path.close()
            drawPath(path, tint)
        }

        Glyph.FUEL -> {
            drawRoundRect(
                color = tint,
                topLeft = at(unit, 4f, 3.5f),
                size = Size(unit * 11f, unit * 17f),
                cornerRadius = CornerRadius(unit * 2f, unit * 2f),
                style = stroke
            )
            drawLine(tint, at(unit, 6.5f, 8.5f), at(unit, 12.5f, 8.5f), unit * 1.6f, StrokeCap.Round)
            drawLine(tint, at(unit, 18f, 9f), at(unit, 18f, 16.5f), unit * 1.8f, StrokeCap.Round)
            drawLine(tint, at(unit, 15.5f, 6.5f), at(unit, 18f, 9f), unit * 1.8f, StrokeCap.Round)
        }

        Glyph.TARGET -> {
            drawCircle(tint, unit * 9.5f, hub, style = stroke)
            drawCircle(tint, unit * 5f, hub, style = thin)
            drawCircle(tint, unit * 1.6f, hub)
        }

        Glyph.CLOCK -> {
            drawCircle(tint, unit * 9.5f, hub, style = stroke)
            drawLine(tint, hub, at(unit, 12f, 6.5f), unit * 1.8f, StrokeCap.Round)
            drawLine(tint, hub, at(unit, 16.5f, 14f), unit * 1.8f, StrokeCap.Round)
        }
    }
}

private fun at(unit: Float, x: Float, y: Float): Offset = Offset(x * unit, y * unit)

private fun polygon(
    unit: Float,
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    x3: Float,
    y3: Float
): Path {
    val path = Path()
    path.moveTo(x1 * unit, y1 * unit)
    path.lineTo(x2 * unit, y2 * unit)
    path.lineTo(x3 * unit, y3 * unit)
    path.close()
    return path
}

private fun starPath(unit: Float, points: Int, outer: Float, inner: Float): Path {
    val path = Path()
    val cx = 12f * unit
    val cy = 12f * unit
    for (index in 0 until points * 2) {
        val radius = if (index % 2 == 0) outer * unit else inner * unit
        val angle = (-90f + index * (180f / points)) * DEG_TO_RAD
        val x = cx + cos(angle) * radius
        val y = cy + sin(angle) * radius
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
