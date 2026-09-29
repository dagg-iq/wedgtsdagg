package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.NightlightRound
import androidx.compose.material.icons.rounded.ShutterSpeed
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.glass.GlassPill
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.widgets.ClockFaceStyle
import com.example.ui.widgets.GlassAnalogClockWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun GlassClockScreen(
    currentStyle: ClockFaceStyle,
    onStyleChange: (ClockFaceStyle) -> Unit,
    isSmoothSweep: Boolean,
    onSweepChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current
    var isSubDialActive by remember { mutableStateOf(true) }
    var isAmbientLumeActive by remember { mutableStateOf(false) }

    var digitalTime by remember { mutableStateOf("") }
    var utcTime by remember { mutableStateOf("") }
    var fullDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val utcFormat = SimpleDateFormat("HH:mm:ss 'UTC'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        while (isActive) {
            val now = Date()
            digitalTime = timeFormat.format(now)
            fullDate = dateFormat.format(now)
            utcTime = utcFormat.format(now)
            delay(1000L)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Clock Title Badge
            Text(
                text = "SWISS HOROLOGY • GLASS EDITION",
                color = tokens.action,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        // Center Clock
        item {
            GlassAnalogClockWidget(
                sizeDp = 290.dp,
                clockStyle = currentStyle,
                isSmoothSweep = isSmoothSweep,
                showSubDial = isSubDialActive
            )
        }

        // Digital & Date Pill Box
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = 6.dp,
                glassAlpha = 0.14f
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = digitalTime.ifEmpty { "10:10:00" },
                            color = tokens.content,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = fullDate.ifEmpty { "Tuesday, September 29" },
                            color = tokens.contentSubtle,
                            fontSize = 12.sp
                        )
                    }

                    // UTC World Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(tokens.light.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = utcTime.ifEmpty { "UTC 07:10" },
                            color = tokens.action,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Clock Style Switcher Pills
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = 6.dp,
                glassAlpha = 0.12f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Face Architecture",
                        color = tokens.contentSubtle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClockStylePill(
                            modifier = Modifier.weight(1f),
                            label = "Chrono",
                            isSelected = currentStyle == ClockFaceStyle.LUXURY_CHRONO,
                            onClick = { onStyleChange(ClockFaceStyle.LUXURY_CHRONO) }
                        )

                        ClockStylePill(
                            modifier = Modifier.weight(1f),
                            label = "Minimal",
                            isSelected = currentStyle == ClockFaceStyle.MINIMAL_GLASS,
                            onClick = { onStyleChange(ClockFaceStyle.MINIMAL_GLASS) }
                        )

                        ClockStylePill(
                            modifier = Modifier.weight(1f),
                            label = "Roman",
                            isSelected = currentStyle == ClockFaceStyle.ROMAN_CLASSIC,
                            onClick = { onStyleChange(ClockFaceStyle.ROMAN_CLASSIC) }
                        )

                        ClockStylePill(
                            modifier = Modifier.weight(1f),
                            label = "Cyber",
                            isSelected = currentStyle == ClockFaceStyle.CYBER_PULSE,
                            onClick = { onStyleChange(ClockFaceStyle.CYBER_PULSE) }
                        )
                    }
                }
            }
        }

        // Toggles: Sub-dial and Sweep Engine
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                    glassAlpha = if (isSmoothSweep) 0.28f else 0.10f,
                    borderAlpha = if (isSmoothSweep) 0.85f else 0.3f,
                    onClick = { onSweepChange(!isSmoothSweep) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Speed,
                            contentDescription = "Sweep Engine",
                            tint = if (isSmoothSweep) tokens.action else tokens.contentSubtle,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = if (isSmoothSweep) "Fluid 60FPS" else "1s Stepper",
                                color = tokens.content,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Seconds Escapement",
                                color = tokens.contentSubtle,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                GlassBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                    glassAlpha = if (isSubDialActive) 0.28f else 0.10f,
                    borderAlpha = if (isSubDialActive) 0.85f else 0.3f,
                    onClick = { isSubDialActive = !isSubDialActive }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShutterSpeed,
                            contentDescription = "Chronograph",
                            tint = if (isSubDialActive) tokens.action else tokens.contentSubtle,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = if (isSubDialActive) "Chronograph On" else "Chronograph Off",
                                color = tokens.content,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Sub-dial Gauge",
                                color = tokens.contentSubtle,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun ClockStylePill(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tokens = LocalGlassTheme.current

    GlassBox(
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = if (isSelected) 3.dp else 1.dp,
        glassAlpha = if (isSelected) 0.35f else 0.08f,
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
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
