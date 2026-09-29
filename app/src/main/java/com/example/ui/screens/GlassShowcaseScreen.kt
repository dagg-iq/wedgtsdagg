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
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.widget.GlassMusicWidgetProvider
import com.example.widget.GlassSystemWidgetProvider
import com.example.widget.GlassWeatherWidgetProvider

@Composable
fun GlassShowcaseScreen(
    currentThemeMode: GlassThemeMode,
    clockStyle: ClockFaceStyle,
    isSmoothSweep: Boolean,
    onNavigateToClock: () -> Unit,
    onNavigateToIcons: (() -> Unit)? = null,
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

        // Featured iOS Glass Icons Banner
        item {
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToIcons?.invoke() },
                shape = RoundedCornerShape(24.dp),
                elevation = 10.dp,
                glassAlpha = 0.22f,
                borderAlpha = 0.85f
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
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(tokens.action.copy(alpha = 0.25f))
                            .border(1.dp, tokens.action.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = tokens.action,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ثيم أيقونات iOS الزجاجي الفاخر",
                            color = tokens.content,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تحويل أيقونات هاتفك إلى طبقات زجاجية مثل iOS 18 + شريط Dock زجاجي",
                            color = tokens.contentSubtle,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(tokens.action)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "فتح",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
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

        // Pin Glass Clock Widget Button
        item {
            GlassPinWidgetCard(
                title = "إضافة ويدجت الساعة والتقويم",
                subtitle = "ضع ويدجت الساعة الزجاجية التفاعلية على شاشتك الرئيسية",
                icon = Icons.Rounded.Schedule,
                iconColor = tokens.action,
                providerClass = GlassAppWidgetProvider::class.java
            )
        }

        // Glass Media Player Widget
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassMediaPlayerWidget()
                GlassPinWidgetCard(
                    title = "إضافة ويدجت مشغل الموسيقى",
                    subtitle = "تحكم بالمقاطع الصوتية والموسيقى مباشرة من الشاشة الرئيسية",
                    icon = Icons.Rounded.Headphones,
                    iconColor = Color(0xFFD18CFF),
                    providerClass = GlassMusicWidgetProvider::class.java
                )
            }
        }

        // Glass Weather & Atmosphere Widget
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassWeatherWidget()
                GlassPinWidgetCard(
                    title = "إضافة ويدجت الطقس ودرجة الحرارة",
                    subtitle = "درجة الحرارة وحالة الطقس المباشرة على شاشتك الرئيسية",
                    icon = Icons.Rounded.WbSunny,
                    iconColor = Color(0xFF4CC3FF),
                    providerClass = GlassWeatherWidgetProvider::class.java
                )
            }
        }

        // Glass Battery & System Health Widget
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassSystemWidget()
                GlassPinWidgetCard(
                    title = "إضافة ويدجت النظام والبطارية",
                    subtitle = "مراقبة نسبة شحن البطارية واستهلاك الذاكرة الحية",
                    icon = Icons.Rounded.Memory,
                    iconColor = Color(0xFF4EEDB3),
                    providerClass = GlassSystemWidgetProvider::class.java
                )
            }
        }

        // Glass Quick Controls Widget
        item {
            GlassControlCenterWidget()
        }

        // Glass Focus Notes & Checklist Widget
        item {
            GlassNotesWidget()
        }

        // Article & Spec Box
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
fun GlassPinWidgetCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    providerClass: Class<*>,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current
    val context = LocalContext.current

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = 6.dp,
        glassAlpha = 0.22f,
        borderAlpha = 0.80f,
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                val provider = ComponentName(context, providerClass)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    appWidgetManager.requestPinAppWidget(provider, null, null)
                } else {
                    Toast.makeText(context, "اضغط مطولاً على الشاشة الرئيسية لإضافة الويدجت", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "اضغط مطولاً على الشاشة الرئيسية لإضافة الويدجت", Toast.LENGTH_LONG).show()
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.20f))
                    .border(1.dp, iconColor.copy(alpha = 0.50f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = tokens.content,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = tokens.contentSubtle,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddHome,
                        contentDescription = "Add",
                        tint = iconColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "إضافة",
                        color = iconColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
