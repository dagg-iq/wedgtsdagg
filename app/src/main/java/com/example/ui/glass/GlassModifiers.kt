package com.example.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.LocalGlassTheme

/**
 * Creates high-fidelity glassmorphism with specular reflections,
 * directional light refraction, and soft depth shadows.
 */
@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 8.dp,
    glassAlpha: Float = 0.14f,
    borderAlpha: Float = 0.45f,
    onClick: (() -> Unit)? = null,
    tokens: GlassColorTokens = LocalGlassTheme.current,
    content: @Composable BoxScope.() -> Unit
) {
    val lightReflex = tokens.glassReflexLight
    val darkReflex = tokens.glassReflexDark

    // Base glass fill with subtle top-to-bottom frosted dispersion
    val baseGlassColor = tokens.glass.copy(alpha = (glassAlpha * (if (tokens.isDark) 1.15f else 0.85f)).coerceIn(0.05f, 0.9f))
    val glassGradient = Brush.linearGradient(
        colors = listOf(
            tokens.light.copy(alpha = (0.16f * lightReflex).coerceIn(0.04f, 0.45f)),
            baseGlassColor,
            tokens.dark.copy(alpha = (0.08f * darkReflex).coerceIn(0.02f, 0.35f))
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    // Specular border gradient (bright top-left to soft dark-rim bottom-right)
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            tokens.light.copy(alpha = (0.75f * lightReflex * borderAlpha).coerceIn(0.1f, 0.95f)),
            tokens.light.copy(alpha = (0.25f * lightReflex * borderAlpha).coerceIn(0.05f, 0.6f)),
            tokens.dark.copy(alpha = (0.15f * darkReflex * borderAlpha).coerceIn(0.02f, 0.5f)),
            tokens.action.copy(alpha = (0.20f * borderAlpha).coerceIn(0.02f, 0.4f))
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = tokens.action),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = tokens.dark.copy(alpha = (0.2f * darkReflex).coerceAtMost(0.6f)),
                spotColor = tokens.action.copy(alpha = (0.25f * darkReflex).coerceAtMost(0.6f))
            )
            .clip(shape)
            .background(brush = glassGradient)
            .border(
                width = 1.2.dp,
                brush = borderBrush,
                shape = shape
            )
            // Inner light reflection curve along the top rim
            .drawBehind {
                val reflexLightAlpha = (0.45f * lightReflex).coerceIn(0.05f, 0.9f)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            tokens.light.copy(alpha = reflexLightAlpha),
                            tokens.light.copy(alpha = reflexLightAlpha * 0.4f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.15f, 1.5f),
                    end = Offset(size.width * 0.85f, 1.5f),
                    strokeWidth = 2.2f
                )
            }
            .then(clickableModifier),
        content = content
    )
}

/**
 * Capsule glass button with high-contrast active and inactive states.
 */
@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalGlassTheme.current
    val shape = CircleShape

    val backgroundAlpha = if (isSelected) 0.36f else 0.10f
    val elevation = if (isSelected) 6.dp else 0.dp

    GlassBox(
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        glassAlpha = backgroundAlpha,
        borderAlpha = if (isSelected) 0.85f else 0.35f,
        onClick = onClick,
        tokens = tokens,
        content = content
    )
}
