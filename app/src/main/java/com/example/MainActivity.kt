package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.glass.GlassBottomNavigation
import com.example.ui.glass.GlassNavTab
import com.example.ui.glass.GlassThemeSwitcher
import com.example.ui.screens.GlassClockScreen
import com.example.ui.screens.GlassControlScreen
import com.example.ui.screens.GlassCustomizerScreen
import com.example.ui.screens.GlassIconThemerScreen
import com.example.ui.screens.GlassShowcaseScreen
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.GlassTheme
import com.example.ui.theme.GlassThemeMode
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.widgets.ClockFaceStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            com.example.widget.GlassAppWidgetProvider.updateAllWidgets(this)
        } catch (_: Exception) {}
        setContent {
            GlassAppRoot()
        }
    }
}

@Composable
fun GlassAppRoot() {
    var themeMode by remember { mutableStateOf(GlassThemeMode.DARK) }
    var clockStyle by remember { mutableStateOf(ClockFaceStyle.LUXURY_CHRONO) }
    var isSmoothSweep by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(GlassNavTab.SHOWCASE) }

    var customActionColor by remember { mutableStateOf<Color?>(null) }
    var lightReflex by remember { mutableFloatStateOf(1.0f) }
    var darkReflex by remember { mutableFloatStateOf(2.0f) }

    // Synchronize reflex defaults when switching base theme
    val activeLightReflex = when (themeMode) {
        GlassThemeMode.LIGHT -> 1.0f
        GlassThemeMode.DARK -> 0.3f
        GlassThemeMode.DIM -> 0.7f
    }
    val activeDarkReflex = when (themeMode) {
        GlassThemeMode.LIGHT -> 1.0f
        GlassThemeMode.DARK -> 2.0f
        GlassThemeMode.DIM -> 2.0f
    }

    GlassTheme(
        mode = themeMode,
        customActionColor = customActionColor
    ) {
        val tokens = LocalGlassTheme.current

        // Handle system back navigation to return to the showcase tab
        if (selectedTab != GlassNavTab.SHOWCASE) {
            BackHandler {
                selectedTab = GlassNavTab.SHOWCASE
            }
        }

        // Full Bleed Atmospheric Glass Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tokens.bg)
                .drawBehind {
                    // Ambient Top Glow Orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                tokens.action.copy(alpha = if (tokens.isDark) 0.16f else 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.8f, size.height * 0.15f),
                            radius = size.width * 0.65f
                        )
                    )
                    // Ambient Bottom Glow Orb
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                tokens.action.copy(alpha = if (tokens.isDark) 0.12f else 0.06f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.1f, size.height * 0.85f),
                            radius = size.width * 0.55f
                        )
                    )
                }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    GlassBottomNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Exact CSS Floating Glass Theme Switcher Capsule (.switcher)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassThemeSwitcher(
                            currentMode = themeMode,
                            onModeChanged = { newMode ->
                                themeMode = newMode
                                customActionColor = null // reset to theme default
                            }
                        )
                    }

                    // Active Tab Screen Content
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_content",
                        modifier = Modifier.weight(1f)
                    ) { tab ->
                        when (tab) {
                            GlassNavTab.SHOWCASE -> {
                                GlassShowcaseScreen(
                                    currentThemeMode = themeMode,
                                    clockStyle = clockStyle,
                                    isSmoothSweep = isSmoothSweep,
                                    onNavigateToClock = { selectedTab = GlassNavTab.CLOCK },
                                    onNavigateToIcons = { selectedTab = GlassNavTab.ICONS }
                                )
                            }
                            GlassNavTab.ICONS -> {
                                GlassIconThemerScreen()
                            }
                            GlassNavTab.CLOCK -> {
                                GlassClockScreen(
                                    currentStyle = clockStyle,
                                    onStyleChange = { clockStyle = it },
                                    isSmoothSweep = isSmoothSweep,
                                    onSweepChange = { isSmoothSweep = it }
                                )
                            }
                            GlassNavTab.CONTROLS -> {
                                GlassControlScreen()
                            }
                            GlassNavTab.CUSTOMIZER -> {
                                GlassCustomizerScreen(
                                    currentStyle = clockStyle,
                                    onStyleChange = { clockStyle = it },
                                    isSmoothSweep = isSmoothSweep,
                                    onSweepChange = { isSmoothSweep = it },
                                    selectedActionColor = customActionColor ?: tokens.action,
                                    onActionColorChange = { customActionColor = it },
                                    lightReflex = lightReflex,
                                    onLightReflexChange = { lightReflex = it },
                                    darkReflex = darkReflex,
                                    onDarkReflexChange = { darkReflex = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
