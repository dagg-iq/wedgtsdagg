package com.example.ui.glass

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.GlassThemeMode
import com.example.ui.theme.LocalGlassTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Recreates the exact CSS `.switcher` with 3 options:
 * Option 1 (Light), Option 2 (Dark), Option 3 (Dim),
 * with 3D inset glass reflections and squash-stretch animation.
 */
@Composable
fun GlassThemeSwitcher(
    currentMode: GlassThemeMode,
    onModeChanged: (GlassThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current
    val coroutineScope = rememberCoroutineScope()

    // Squash & stretch scale factor simulation (scaleToggle animation)
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(currentMode) {
        scaleAnim.animateTo(1.22f, animationSpec = tween(120, easing = FastOutSlowInEasing))
        scaleAnim.animateTo(1.0f, animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium))
    }

    val selectedIndex = when (currentMode) {
        GlassThemeMode.LIGHT -> 0
        GlassThemeMode.DARK -> 1
        GlassThemeMode.DIM -> 2
    }

    // Outer Switcher dimensions (approx 244dp x 66dp)
    val totalWidth = 244.dp
    val totalHeight = 64.dp
    val pillPadding = 4.dp
    val pillWidth = (totalWidth - (pillPadding * 2)) / 3

    val targetOffsetX = pillPadding + (pillWidth * selectedIndex)
    val animatedOffsetX by animateDpAsState(
        targetValue = targetOffsetX,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "pill_offset"
    )

    val lightReflex = tokens.glassReflexLight
    val darkReflex = tokens.glassReflexDark

    // Outer switcher glass container
    Box(
        modifier = modifier
            .width(totalWidth)
            .height(totalHeight)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = tokens.dark.copy(alpha = (0.22f * darkReflex).coerceAtMost(0.6f)),
                spotColor = tokens.action.copy(alpha = (0.18f * darkReflex).coerceAtMost(0.5f))
            )
            .clip(CircleShape)
            // background-color: color-mix(in srgb, var(--c-glass) 12%, transparent);
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        tokens.light.copy(alpha = (0.18f * lightReflex).coerceIn(0.06f, 0.45f)),
                        tokens.glass.copy(alpha = 0.14f),
                        tokens.dark.copy(alpha = (0.12f * darkReflex).coerceIn(0.04f, 0.35f))
                    )
                )
            )
            .border(
                width = 1.3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        tokens.light.copy(alpha = (0.85f * lightReflex).coerceIn(0.15f, 0.95f)),
                        tokens.light.copy(alpha = 0.20f),
                        tokens.dark.copy(alpha = (0.25f * darkReflex).coerceIn(0.05f, 0.5f)),
                        tokens.action.copy(alpha = 0.30f)
                    )
                ),
                shape = CircleShape
            )
            .drawBehind {
                // Top inner highlight curve (inset 1.8px 3px 0px -2px)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            tokens.light.copy(alpha = (0.75f * lightReflex).coerceIn(0.1f, 0.95f)),
                            tokens.light.copy(alpha = (0.35f * lightReflex).coerceIn(0.05f, 0.5f)),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.12f, 1.8f),
                    end = Offset(size.width * 0.88f, 1.8f),
                    strokeWidth = 2.4f
                )
                // Bottom inner dark shadow rim (inset 0px 3px 4px -2px)
                drawLine(
                    color = tokens.dark.copy(alpha = (0.22f * darkReflex).coerceIn(0.04f, 0.45f)),
                    start = Offset(size.width * 0.2f, size.height - 1.5f),
                    end = Offset(size.width * 0.8f, size.height - 1.5f),
                    strokeWidth = 2.0f
                )
            }
            .testTag("glass_theme_switcher")
    ) {
        // Sliding Active Indicator Capsule (`switcher::after`)
        // background-color: color-mix(in srgb, var(--c-glass) 36%, transparent);
        Box(
            modifier = Modifier
                .offset { IntOffset(x = animatedOffsetX.roundToPx(), y = pillPadding.roundToPx()) }
                .width(pillWidth)
                .height(totalHeight - (pillPadding * 2))
                .scale(scaleX = scaleAnim.value, scaleY = 2f - scaleAnim.value)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = tokens.dark.copy(alpha = 0.25f),
                    spotColor = tokens.action.copy(alpha = 0.35f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            tokens.light.copy(alpha = (0.30f * lightReflex).coerceIn(0.1f, 0.6f)),
                            tokens.glass.copy(alpha = 0.38f),
                            tokens.action.copy(alpha = 0.22f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            tokens.light.copy(alpha = (0.90f * lightReflex).coerceIn(0.2f, 0.98f)),
                            tokens.light.copy(alpha = 0.35f),
                            tokens.action.copy(alpha = 0.65f)
                        )
                    ),
                    shape = CircleShape
                )
                .drawBehind {
                    // Crisp top specular highlight inside the pill
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                tokens.light.copy(alpha = 0.85f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(size.width * 0.15f, 1.5f),
                        end = Offset(size.width * 0.85f, 1.5f),
                        strokeWidth = 2f
                    )
                }
        )

        // 3 Interactive Options
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeOptionItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = Icons.Rounded.WbSunny,
                label = "Light",
                isSelected = currentMode == GlassThemeMode.LIGHT,
                tokens = tokens,
                onClick = { onModeChanged(GlassThemeMode.LIGHT) }
            )

            ThemeOptionItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = Icons.Rounded.Nightlight,
                label = "Dark",
                isSelected = currentMode == GlassThemeMode.DARK,
                tokens = tokens,
                onClick = { onModeChanged(GlassThemeMode.DARK) }
            )

            ThemeOptionItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = Icons.Rounded.AutoAwesome,
                label = "Dim",
                isSelected = currentMode == GlassThemeMode.DIM,
                tokens = tokens,
                onClick = { onModeChanged(GlassThemeMode.DIM) }
            )
        }
    }
}

@Composable
private fun ThemeOptionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    tokens: GlassColorTokens,
    onClick: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 0.95f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "icon_scale"
    )

    val iconColor = if (isSelected) {
        tokens.action
    } else {
        tokens.content.copy(alpha = 0.70f)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .semantics { contentDescription = "Switch to $label theme" }
            .testTag("theme_btn_$label"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier
                .size(23.dp)
                .scale(iconScale)
        )
    }
}
