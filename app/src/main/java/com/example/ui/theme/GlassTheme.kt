package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

/**
 * Exact color palette and glass reflection physics derived from the specification:
 * - Light: --c-bg: #E8E8E9, --c-glass: #bbbbbc, --c-content: #224, --c-action: #0052f5
 * - Dark:  --c-bg: #1b1b1d, --c-glass: #bbbbbc, --c-content: #e1e1e1, --c-action: #03d5ff
 * - Dim:   --c-bg: #152433, --c-glass: hsl(335 250% 74% / 1) ~ #FF7BB8, --c-content: #d5dbe2, --c-action: #ff48a9
 */
enum class GlassThemeMode {
    LIGHT,
    DARK,
    DIM
}

@Immutable
data class GlassColorTokens(
    val bg: Color,
    val glass: Color,
    val light: Color,
    val dark: Color,
    val content: Color,
    val contentSubtle: Color,
    val action: Color,
    val actionGlow: Color,
    val glassReflexDark: Float,
    val glassReflexLight: Float,
    val saturation: Float,
    val isDark: Boolean
)

val LightGlassTokens = GlassColorTokens(
    bg = Color(0xFFE8E8E9),
    glass = Color(0xFFBBBBBC),
    light = Color(0xFFFFFFFF),
    dark = Color(0xFF000000),
    content = Color(0xFF222244),
    contentSubtle = Color(0xFF555577),
    action = Color(0xFF0052F5),
    actionGlow = Color(0x330052F5),
    glassReflexDark = 1.0f,
    glassReflexLight = 1.0f,
    saturation = 1.5f,
    isDark = false
)

val DarkGlassTokens = GlassColorTokens(
    bg = Color(0xFF1B1B1D),
    glass = Color(0xFFBBBBBC),
    light = Color(0xFFFFFFFF),
    dark = Color(0xFF000000),
    content = Color(0xFFE1E1E1),
    contentSubtle = Color(0xFF8E8E93),
    action = Color(0xFF03D5FF),
    actionGlow = Color(0x4003D5FF),
    glassReflexDark = 2.0f,
    glassReflexLight = 0.3f,
    saturation = 1.5f,
    isDark = true
)

val DimGlassTokens = GlassColorTokens(
    bg = Color(0xFF152433),
    glass = Color(0xFFFF7BB8), // hsl(335, 100%, 74%)
    light = Color(0xFF99DEFF),
    dark = Color(0xFF20001B),
    content = Color(0xFFD5DBE2),
    contentSubtle = Color(0xFF88A0B8),
    action = Color(0xFFFF48A9),
    actionGlow = Color(0x40FF48A9),
    glassReflexDark = 2.0f,
    glassReflexLight = 0.7f,
    saturation = 2.0f,
    isDark = true
)

val LocalGlassTheme = compositionLocalOf { DarkGlassTokens }

@Composable
fun GlassTheme(
    mode: GlassThemeMode = GlassThemeMode.DARK,
    customActionColor: Color? = null,
    customGlassOpacity: Float? = null,
    content: @Composable () -> Unit
) {
    val targetTokens = when (mode) {
        GlassThemeMode.LIGHT -> LightGlassTokens
        GlassThemeMode.DARK -> DarkGlassTokens
        GlassThemeMode.DIM -> DimGlassTokens
    }

    // 400ms smooth cubic-bezier / spring transition across color tokens
    val springSpec = spring<Color>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
    val floatSpringSpec = spring<Float>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)

    val animatedBg by animateColorAsState(targetTokens.bg, animationSpec = springSpec, label = "bg")
    val animatedGlass by animateColorAsState(targetTokens.glass, animationSpec = springSpec, label = "glass")
    val animatedLight by animateColorAsState(targetTokens.light, animationSpec = springSpec, label = "light")
    val animatedDark by animateColorAsState(targetTokens.dark, animationSpec = springSpec, label = "dark")
    val animatedContent by animateColorAsState(targetTokens.content, animationSpec = springSpec, label = "content")
    val animatedContentSubtle by animateColorAsState(targetTokens.contentSubtle, animationSpec = springSpec, label = "contentSubtle")
    val targetAction = customActionColor ?: targetTokens.action
    val animatedAction by animateColorAsState(targetAction, animationSpec = springSpec, label = "action")
    val animatedActionGlow by animateColorAsState(targetAction.copy(alpha = 0.35f), animationSpec = springSpec, label = "actionGlow")

    val animatedDarkReflex by animateFloatAsState(targetTokens.glassReflexDark, animationSpec = floatSpringSpec, label = "darkReflex")
    val animatedLightReflex by animateFloatAsState(targetTokens.glassReflexLight, animationSpec = floatSpringSpec, label = "lightReflex")
    val animatedSaturation by animateFloatAsState(targetTokens.saturation, animationSpec = floatSpringSpec, label = "saturation")

    val activeTokens = GlassColorTokens(
        bg = animatedBg,
        glass = animatedGlass,
        light = animatedLight,
        dark = animatedDark,
        content = animatedContent,
        contentSubtle = animatedContentSubtle,
        action = animatedAction,
        actionGlow = animatedActionGlow,
        glassReflexDark = animatedDarkReflex,
        glassReflexLight = animatedLightReflex,
        saturation = animatedSaturation,
        isDark = targetTokens.isDark
    )

    val materialColorScheme: ColorScheme = if (targetTokens.isDark) {
        darkColorScheme(
            primary = animatedAction,
            background = animatedBg,
            surface = animatedBg,
            onPrimary = Color.Black,
            onBackground = animatedContent,
            onSurface = animatedContent
        )
    } else {
        lightColorScheme(
            primary = animatedAction,
            background = animatedBg,
            surface = animatedBg,
            onPrimary = Color.White,
            onBackground = animatedContent,
            onSurface = animatedContent
        )
    }

    CompositionLocalProvider(LocalGlassTheme provides activeTokens) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography,
            content = content
        )
    }
}
