package com.example.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.LocalGlassTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class ClockFaceStyle {
    LUXURY_CHRONO,
    ROMAN_CLASSIC,
    MINIMAL_GLASS,
    CYBER_PULSE
}

@Composable
fun GlassAnalogClockWidget(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 270.dp,
    clockStyle: ClockFaceStyle = ClockFaceStyle.LUXURY_CHRONO,
    isSmoothSweep: Boolean = true,
    showSubDial: Boolean = true,
    onClockClick: (() -> Unit)? = null
) {
    val tokens = LocalGlassTheme.current

    // Live continuous time calculation
    var currentTimeMillis by remember { mutableFloatStateOf(System.currentTimeMillis().toFloat()) }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(isSmoothSweep) {
        while (isActive) {
            val now = System.currentTimeMillis()
            currentTimeMillis = now.toFloat()
            calendar = Calendar.getInstance()
            // 60 FPS for smooth sweep, 500ms for mechanical tick
            delay(if (isSmoothSweep) 16L else 200L)
        }
    }

    val cal = calendar
    val hour = cal.get(Calendar.HOUR)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val millisecond = cal.get(Calendar.MILLISECOND)

    val sweepSecondFraction = if (isSmoothSweep) {
        second + (millisecond / 1000f)
    } else {
        second.toFloat()
    }

    val secondAngle = (sweepSecondFraction / 60f) * 360f
    val minuteAngle = ((minute + (sweepSecondFraction / 60f)) / 60f) * 360f
    val hourAngle = (((hour % 12) + (minute / 60f) + (sweepSecondFraction / 3600f)) / 12f) * 360f

    val dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())?.uppercase() ?: "SAT"
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())?.uppercase() ?: "SEP"

    // Infinite breathing glow for Cyber/Dim style
    val infiniteTransition = rememberInfiniteTransition(label = "clock_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    GlassBox(
        modifier = modifier
            .size(sizeDp)
            .testTag("glass_analog_clock_card"),
        shape = CircleShape,
        elevation = 12.dp,
        glassAlpha = 0.16f,
        borderAlpha = 0.65f,
        onClick = onClockClick
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.94f

            // 1. Outer Multi-layer Bevel Glass Ring
            drawClockDialBase(
                center = center,
                radius = radius,
                tokens = tokens,
                pulseAlpha = pulseAlpha,
                clockStyle = clockStyle
            )

            // 2. Hour indices and minute ticks
            drawClockIndices(
                center = center,
                radius = radius,
                tokens = tokens,
                style = clockStyle
            )

            // 3. Sub-dials (Chronograph / Seconds / Battery gauge)
            if (showSubDial && clockStyle != ClockFaceStyle.MINIMAL_GLASS) {
                drawSubDials(
                    center = center,
                    radius = radius,
                    sweepSecondFraction = sweepSecondFraction,
                    tokens = tokens,
                    style = clockStyle
                )
            }

            // 4. Date and Day Complication Box
            drawDateComplication(
                center = center,
                radius = radius,
                dayOfWeek = dayOfWeek,
                dayOfMonth = dayOfMonth,
                tokens = tokens
            )

            // 5. Hour Hand
            rotate(hourAngle, pivot = center) {
                drawHourHand(center, radius, tokens)
            }

            // 6. Minute Hand
            rotate(minuteAngle, pivot = center) {
                drawMinuteHand(center, radius, tokens)
            }

            // 7. Sweeping Seconds Hand
            rotate(secondAngle, pivot = center) {
                drawSecondsHand(center, radius, tokens, pulseAlpha)
            }

            // 8. Multi-Stage Glass Jewel Pinion Screw
            drawCenterPinion(center, tokens)
        }

        // Digital micro-time badge at bottom center
        Text(
            text = String.format(Locale.US, "%02d:%02d:%02d", (if (hour == 0) 12 else hour), minute, second),
            color = tokens.contentSubtle,
            fontSize = (sizeDp.value * 0.042f).sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = (sizeDp.value * 0.14f).dp)
        )
    }
}

private fun DrawScope.drawClockDialBase(
    center: Offset,
    radius: Float,
    tokens: GlassColorTokens,
    pulseAlpha: Float,
    clockStyle: ClockFaceStyle
) {
    val lightReflex = tokens.glassReflexLight
    val darkReflex = tokens.glassReflexDark

    // Outer Beveled Glass Rim
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                tokens.light.copy(alpha = (0.28f * lightReflex).coerceIn(0.08f, 0.55f)),
                tokens.glass.copy(alpha = 0.16f),
                tokens.dark.copy(alpha = (0.18f * darkReflex).coerceIn(0.05f, 0.45f))
            ),
            center = Offset(center.x - radius * 0.3f, center.y - radius * 0.35f),
            radius = radius * 1.3f
        ),
        radius = radius,
        center = center
    )

    // Inner Concentric Glass Dial (sunken crystal disc)
    val innerRadius = radius * 0.88f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                tokens.glass.copy(alpha = 0.22f),
                tokens.dark.copy(alpha = (0.15f * darkReflex).coerceIn(0.04f, 0.40f)),
                tokens.action.copy(alpha = (0.10f * pulseAlpha).coerceAtMost(0.25f))
            ),
            center = center,
            radius = innerRadius
        ),
        radius = innerRadius,
        center = center
    )

    // Specular Top-Left Arch Light Reflection (glass refraction arc)
    drawArc(
        brush = Brush.sweepGradient(
            0.0f to Color.Transparent,
            0.2f to tokens.light.copy(alpha = (0.85f * lightReflex).coerceIn(0.2f, 0.95f)),
            0.4f to tokens.light.copy(alpha = (0.20f * lightReflex).coerceIn(0.05f, 0.4f)),
            0.6f to Color.Transparent,
            center = center
        ),
        startAngle = 190f,
        sweepAngle = 100f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )

    // Bottom-Right Ambient Rim Shade
    drawArc(
        color = tokens.dark.copy(alpha = (0.35f * darkReflex).coerceIn(0.1f, 0.6f)),
        startAngle = 20f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
    )

    // Subtle hairline track at radius * 0.88
    drawCircle(
        color = tokens.light.copy(alpha = (0.20f * lightReflex).coerceIn(0.05f, 0.4f)),
        radius = innerRadius,
        center = center,
        style = Stroke(width = 1.0f)
    )
}

private fun DrawScope.drawClockIndices(
    center: Offset,
    radius: Float,
    tokens: GlassColorTokens,
    style: ClockFaceStyle
) {
    val lightReflex = tokens.glassReflexLight
    val trackRadius = radius * 0.84f

    for (i in 0 until 60) {
        val angleRad = (i * 6f) * (PI / 180f).toFloat() - (PI / 2f).toFloat()
        val isMajorHour = i % 5 == 0
        val isCardinal = i % 15 == 0 // 12, 3, 6, 9

        if (isMajorHour) {
            val markerLength = if (isCardinal) radius * 0.14f else radius * 0.10f
            val startR = trackRadius - markerLength
            val endR = trackRadius

            val startX = center.x + cos(angleRad) * startR
            val startY = center.y + sin(angleRad) * startR
            val endX = center.x + cos(angleRad) * endR
            val endY = center.y + sin(angleRad) * endR

            val strokeW = if (isCardinal) 4.2f else 3.0f

            // Faceted Hour Baton Marker with luminous center
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        tokens.light.copy(alpha = (0.95f * lightReflex).coerceIn(0.3f, 1f)),
                        tokens.action.copy(alpha = 0.85f)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                ),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        } else if (style != ClockFaceStyle.MINIMAL_GLASS) {
            // Minute/Second glass tick dot
            val dotR = trackRadius - (radius * 0.02f)
            val dotX = center.x + cos(angleRad) * dotR
            val dotY = center.y + sin(angleRad) * dotR

            drawCircle(
                color = tokens.light.copy(alpha = (0.35f * lightReflex).coerceIn(0.1f, 0.6f)),
                radius = 1.8f,
                center = Offset(dotX, dotY)
            )
        }
    }
}

private fun DrawScope.drawSubDials(
    center: Offset,
    radius: Float,
    sweepSecondFraction: Float,
    tokens: GlassColorTokens,
    style: ClockFaceStyle
) {
    // Mini sub-dial at 6 o'clock (Chronograph seconds / Battery)
    val subCenter = Offset(center.x, center.y + (radius * 0.42f))
    val subRadius = radius * 0.22f

    // Sub-dial glass disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                tokens.dark.copy(alpha = 0.20f),
                tokens.glass.copy(alpha = 0.15f),
                tokens.light.copy(alpha = 0.12f)
            ),
            center = subCenter,
            radius = subRadius
        ),
        radius = subRadius,
        center = subCenter
    )

    // Sub-dial rim
    drawCircle(
        color = tokens.light.copy(alpha = 0.40f),
        radius = subRadius,
        center = subCenter,
        style = Stroke(width = 1.2f)
    )

    // Sub-dial ticks (0, 15, 30, 45)
    for (step in 0 until 4) {
        val rad = (step * 90f) * (PI / 180f).toFloat() - (PI / 2f).toFloat()
        val sX = subCenter.x + cos(rad) * (subRadius * 0.7f)
        val sY = subCenter.y + sin(rad) * (subRadius * 0.7f)
        val eX = subCenter.x + cos(rad) * subRadius
        val eY = subCenter.y + sin(rad) * subRadius

        drawLine(
            color = tokens.light.copy(alpha = 0.65f),
            start = Offset(sX, sY),
            end = Offset(eX, eY),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }

    // Sub-dial needle pointing to seconds fraction
    val subAngle = (sweepSecondFraction / 60f) * 360f
    rotate(subAngle, pivot = subCenter) {
        drawLine(
            color = tokens.action,
            start = subCenter,
            end = Offset(subCenter.x, subCenter.y - (subRadius * 0.82f)),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = tokens.light,
            radius = 2.2f,
            center = subCenter
        )
    }
}

private fun DrawScope.drawDateComplication(
    center: Offset,
    radius: Float,
    dayOfWeek: String,
    dayOfMonth: Int,
    tokens: GlassColorTokens
) {
    // Glass date pill at 3 o'clock
    val dateWidth = radius * 0.36f
    val dateHeight = radius * 0.14f
    val dateX = center.x + (radius * 0.34f)
    val dateY = center.y - (dateHeight / 2f)

    // Frosted cutout window
    drawRoundRect(
        color = tokens.dark.copy(alpha = 0.22f),
        topLeft = Offset(dateX, dateY),
        size = Size(dateWidth, dateHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
    )

    drawRoundRect(
        color = tokens.light.copy(alpha = 0.45f),
        topLeft = Offset(dateX, dateY),
        size = Size(dateWidth, dateHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
        style = Stroke(width = 1.2f)
    )

    // Inner bevel highlight
    drawLine(
        color = tokens.light.copy(alpha = 0.7f),
        start = Offset(dateX + 3f, dateY + 1f),
        end = Offset(dateX + dateWidth - 3f, dateY + 1f),
        strokeWidth = 1f
    )
}

private fun DrawScope.drawHourHand(center: Offset, radius: Float, tokens: GlassColorTokens) {
    val handLength = radius * 0.52f
    val handWidth = radius * 0.058f
    val tailLength = radius * 0.12f

    val path = Path().apply {
        moveTo(center.x - handWidth / 2f, center.y + tailLength)
        lineTo(center.x + handWidth / 2f, center.y + tailLength)
        lineTo(center.x + handWidth / 2f, center.y - handLength * 0.75f)
        lineTo(center.x, center.y - handLength)
        lineTo(center.x - handWidth / 2f, center.y - handLength * 0.75f)
        close()
    }

    // Hand drop shadow on the glass dial
    drawPath(
        path = path,
        color = tokens.dark.copy(alpha = 0.28f),
        style = Fill
    )

    // Metallic Glass Hand body
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(
                tokens.light.copy(alpha = 0.95f),
                tokens.glass.copy(alpha = 0.85f),
                tokens.content.copy(alpha = 0.75f)
            ),
            start = Offset(center.x - handWidth, center.y),
            end = Offset(center.x + handWidth, center.y)
        ),
        style = Fill
    )

    // Center Crease Bevel Line
    drawLine(
        color = tokens.light,
        start = Offset(center.x, center.y + tailLength * 0.5f),
        end = Offset(center.x, center.y - handLength * 0.9f),
        strokeWidth = 1.4f
    )
}

private fun DrawScope.drawMinuteHand(center: Offset, radius: Float, tokens: GlassColorTokens) {
    val handLength = radius * 0.76f
    val handWidth = radius * 0.044f
    val tailLength = radius * 0.15f

    val path = Path().apply {
        moveTo(center.x - handWidth / 2f, center.y + tailLength)
        lineTo(center.x + handWidth / 2f, center.y + tailLength)
        lineTo(center.x + (handWidth * 0.4f), center.y - handLength * 0.85f)
        lineTo(center.x, center.y - handLength)
        lineTo(center.x - (handWidth * 0.4f), center.y - handLength * 0.85f)
        close()
    }

    // Hand soft shadow
    drawPath(
        path = path,
        color = tokens.dark.copy(alpha = 0.32f),
        style = Fill
    )

    // Hand fill
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(
                tokens.light,
                tokens.light.copy(alpha = 0.85f),
                tokens.action.copy(alpha = 0.5f)
            ),
            start = Offset(center.x - handWidth, center.y),
            end = Offset(center.x + handWidth, center.y)
        ),
        style = Fill
    )

    // Center specular lumen line
    drawLine(
        color = tokens.light,
        start = Offset(center.x, center.y + tailLength * 0.4f),
        end = Offset(center.x, center.y - handLength * 0.95f),
        strokeWidth = 1.2f
    )
}

private fun DrawScope.drawSecondsHand(
    center: Offset,
    radius: Float,
    tokens: GlassColorTokens,
    pulseAlpha: Float
) {
    val handLength = radius * 0.88f
    val tailLength = radius * 0.22f
    val actionColor = tokens.action

    // Ambient glow along seconds hand
    drawLine(
        color = actionColor.copy(alpha = 0.35f * pulseAlpha),
        start = Offset(center.x, center.y + tailLength),
        end = Offset(center.x, center.y - handLength),
        strokeWidth = 4.2f,
        cap = StrokeCap.Round
    )

    // Razor-sharp needle
    drawLine(
        color = actionColor,
        start = Offset(center.x, center.y + tailLength),
        end = Offset(center.x, center.y - handLength),
        strokeWidth = 2.0f,
        cap = StrokeCap.Round
    )

    // Circular counterbalance ring on the tail
    val counterCenter = Offset(center.x, center.y + tailLength * 0.65f)
    val counterRadius = radius * 0.038f

    drawCircle(
        color = actionColor,
        radius = counterRadius,
        center = counterCenter,
        style = Stroke(width = 2.0f)
    )

    // Illuminated second tip arrow/dot
    drawCircle(
        color = tokens.light,
        radius = 2.8f,
        center = Offset(center.x, center.y - handLength)
    )
}

private fun DrawScope.drawCenterPinion(center: Offset, tokens: GlassColorTokens) {
    // Outer metallic ring
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                tokens.light,
                tokens.glass,
                tokens.dark
            ),
            center = center,
            radius = 11f
        ),
        radius = 11f,
        center = center
    )

    // Inner jewel core
    drawCircle(
        color = tokens.action,
        radius = 6.5f,
        center = center
    )

    // Specular center light reflection
    drawCircle(
        color = Color.White,
        radius = 2.4f,
        center = Offset(center.x - 2f, center.y - 2f)
    )
}
