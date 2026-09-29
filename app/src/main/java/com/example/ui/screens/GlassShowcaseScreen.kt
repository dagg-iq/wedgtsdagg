package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.Toast
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
import androidx.compose.material.icons.rounded.AddHome
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.GlassThemeMode
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.widgets.ClockFaceStyle
import com.example.ui.widgets.GlassAnalogClockWidget
import com.example.ui.widgets.GlassControlCenterWidget
import com.example.ui.widgets.GlassMediaPlayerWidget
import com.example.ui.widgets.GlassNotesWidget
import com.example.ui.widgets.GlassSystemWidget
import com.example.ui.widgets.GlassWeatherWidget
import com.example.widget.GlassAppWidgetProvider

@Composable
fun GlassShowcaseScreen(
    currentThemeMode: GlassThemeMode,
    clockStyle: ClockFaceStyle,
    isSmoothSweep: Boolean,
    onNavigateToClock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Hero Glass Analog Clock Card
        item {
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_clock_card"),
                shape = RoundedCornerShape(28.dp),
                elevation = 12.dp,
                glassAlpha = 0.16f,
                borderAlpha = 0.70f,
                onClick = onNavigateToClock
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ANALOG HOROLOGY",
                            color = tokens.action,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Full View",
                                color = tokens.contentSubtle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Rounded.OpenInNew,
                                contentDescription = "Open",
                                tint = tokens.contentSubtle,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    GlassAnalogClockWidget(
                        sizeDp = 210.dp,
                        clockStyle = clockStyle,
                        isSmoothSweep = isSmoothSweep,
                        showSubDial = true,
                        onClockClick = onNavigateToClock
                    )

                    Text(
                        text = "Touch to inspect escapement & dials",
                        color = tokens.contentSubtle,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Home Screen Widget Pin Card
        item {
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pin_widget_card"),
                shape = RoundedCornerShape(22.dp),
                elevation = 8.dp,
                glassAlpha = 0.22f,
                borderAlpha = 0.85f,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                        val provider = ComponentName(context, GlassAppWidgetProvider::class.java)
                        if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                            appWidgetManager.requestPinAppWidget(provider, null, null)
                        } else {
                            Toast.makeText(context, "Long-press your Home Screen to add Glass Widget", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Long-press your Home Screen to add Glass Widget", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(tokens.action.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddHome,
                            contentDescription = "Pin Widget",
                            tint = tokens.action,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add to Home Screen",
                            color = tokens.content,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pin real Glass Clock & Quick Switcher to your launcher",
                            color = tokens.contentSubtle,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Glass Quick Controls Widget
        item {
            GlassControlCenterWidget()
        }

        // Glass Weather & Atmosphere Widget
        item {
            GlassWeatherWidget()
        }

        // Glass Media Player Widget
        item {
            GlassMediaPlayerWidget()
        }

        // Glass Battery & System Health Widget
        item {
            GlassSystemWidget()
        }

        // Glass Focus Notes & Checklist Widget
        item {
            GlassNotesWidget()
        }

        // Article & Spec Box (Translating CSS .box, .article, blockquote)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(tokens.action.copy(alpha = 0.08f))
                    .border(
                        width = 1.dp,
                        color = tokens.action.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "Spec",
                            tint = tokens.action,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Reflex Glass Architecture",
                            color = tokens.content,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "\"Light refraction and specular highlights dynamically adapt when shifting between Light, Dark, and Dim modes with fluid spring interpolation.\"",
                        color = tokens.contentSubtle,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SpecMetric(label = "Reflex Light", value = "${(tokens.glassReflexLight * 100).toInt()}%", color = tokens.action)
                        SpecMetric(label = "Reflex Dark", value = "${(tokens.glassReflexDark * 100).toInt()}%", color = tokens.action)
                        SpecMetric(label = "Saturation", value = "${(tokens.saturation * 100).toInt()}%", color = tokens.action)
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
private fun SpecMetric(label: String, value: String, color: Color) {
    Column {
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = color.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}
