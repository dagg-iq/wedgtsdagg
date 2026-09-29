package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.ShutterSpeed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.widgets.ClockFaceStyle
import com.example.ui.widgets.GlassAnalogClockWidget

@Composable
fun GlassCustomizerScreen(
    currentStyle: ClockFaceStyle,
    onStyleChange: (ClockFaceStyle) -> Unit,
    isSmoothSweep: Boolean,
    onSweepChange: (Boolean) -> Unit,
    selectedActionColor: Color,
    onActionColorChange: (Color) -> Unit,
    lightReflex: Float,
    onLightReflexChange: (Float) -> Unit,
    darkReflex: Float,
    onDarkReflexChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    val presetColors = listOf(
        Color(0xFF03D5FF) to "Cyan",
        Color(0xFF0052F5) to "Royal",
        Color(0xFFFF48A9) to "Neon Pink",
        Color(0xFF00F5A0) to "Emerald",
        Color(0xFFFF9500) to "Amber",
        Color(0xFFBF5AF2) to "Violet"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Live Preview of the Clock with customized settings
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Live Glass Preview",
                    color = tokens.contentSubtle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                GlassAnalogClockWidget(
                    sizeDp = 220.dp,
                    clockStyle = currentStyle,
                    isSmoothSweep = isSmoothSweep,
                    showSubDial = true
                )
            }
        }

        // Section 1: Clock Dial Style
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 6.dp,
                glassAlpha = 0.14f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Watch,
                            contentDescription = "Clock Style",
                            tint = tokens.action,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Clock Dial Architecture",
                            color = tokens.content,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClockStyleOption(
                            modifier = Modifier.weight(1f),
                            label = "Chrono",
                            isSelected = currentStyle == ClockFaceStyle.LUXURY_CHRONO,
                            onClick = { onStyleChange(ClockFaceStyle.LUXURY_CHRONO) }
                        )

                        ClockStyleOption(
                            modifier = Modifier.weight(1f),
                            label = "Minimal",
                            isSelected = currentStyle == ClockFaceStyle.MINIMAL_GLASS,
                            onClick = { onStyleChange(ClockFaceStyle.MINIMAL_GLASS) }
                        )

                        ClockStyleOption(
                            modifier = Modifier.weight(1f),
                            label = "Roman",
                            isSelected = currentStyle == ClockFaceStyle.ROMAN_CLASSIC,
                            onClick = { onStyleChange(ClockFaceStyle.ROMAN_CLASSIC) }
                        )

                        ClockStyleOption(
                            modifier = Modifier.weight(1f),
                            label = "Cyber",
                            isSelected = currentStyle == ClockFaceStyle.CYBER_PULSE,
                            onClick = { onStyleChange(ClockFaceStyle.CYBER_PULSE) }
                        )
                    }
                }
            }
        }

        // Section 2: Second Hand Movement (Sweep vs Tick)
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 6.dp,
                glassAlpha = 0.14f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShutterSpeed,
                            contentDescription = "Hand Motion",
                            tint = tokens.action,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Seconds Hand Motion Engine",
                            color = tokens.content,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClockStyleOption(
                            modifier = Modifier.weight(1f),
                            label = "60 FPS Fluid Sweep",
                            isSelected = isSmoothSweep,
                            onClick = { onSweepChange(true) }
                        )

                        ClockStyleOption(
                            modifier = Modifier.weight(1f),
                            label = "Mechanical 1s Tick",
                            isSelected = !isSmoothSweep,
                            onClick = { onSweepChange(false) }
                        )
                    }
                }
            }
        }

        // Section 3: Accent / Action Color Swatches
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 6.dp,
                glassAlpha = 0.14f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = "Palette",
                            tint = tokens.action,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Glass Refraction Accent Color",
                            color = tokens.content,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presetColors.forEach { (color, name) ->
                            val isSelected = selectedActionColor == color
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) tokens.light else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onActionColorChange(color) }
                                    .testTag("color_swatch_$name"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Glass Reflection Physics Tuners
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 6.dp,
                glassAlpha = 0.14f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Physics",
                            tint = tokens.action,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Glass Reflection Intensity",
                            color = tokens.content,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // Light reflex slider
                    Text(
                        text = "Light Bevel Reflex: ${(lightReflex * 100).toInt()}%",
                        color = tokens.contentSubtle,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = lightReflex,
                        onValueChange = onLightReflexChange,
                        valueRange = 0.1f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = tokens.action,
                            activeTrackColor = tokens.action,
                            inactiveTrackColor = tokens.light.copy(alpha = 0.2f)
                        )
                    )

                    // Dark reflex slider
                    Text(
                        text = "Depth Shadow Reflex: ${(darkReflex * 100).toInt()}%",
                        color = tokens.contentSubtle,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = darkReflex,
                        onValueChange = onDarkReflexChange,
                        valueRange = 0.5f..3.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = tokens.action,
                            activeTrackColor = tokens.action,
                            inactiveTrackColor = tokens.light.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun ClockStyleOption(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tokens = LocalGlassTheme.current

    GlassBox(
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = if (isSelected) 4.dp else 1.dp,
        glassAlpha = if (isSelected) 0.36f else 0.08f,
        borderAlpha = if (isSelected) 0.85f else 0.25f,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) tokens.action else tokens.contentSubtle,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
