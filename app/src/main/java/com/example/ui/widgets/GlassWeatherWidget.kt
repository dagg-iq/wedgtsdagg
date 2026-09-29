package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
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
import com.example.ui.theme.LocalGlassTheme

@Composable
fun GlassWeatherWidget(
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    GlassBox(
        modifier = modifier
            .fillMaxWidth()
            .testTag("glass_weather_widget"),
        shape = RoundedCornerShape(26.dp),
        elevation = 10.dp,
        glassAlpha = 0.16f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // City & Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Location",
                        tint = tokens.action,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Riyadh, SA",
                        color = tokens.content,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Text(
                    text = "Clear Sky",
                    color = tokens.contentSubtle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Big Temperature & Glass Weather Art
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "28",
                            color = tokens.content,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "°C",
                            color = tokens.action,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Text(
                        text = "High 34° • Low 22°",
                        color = tokens.contentSubtle,
                        fontSize = 12.sp
                    )
                }

                // Glowing Glass Weather Sun/Cloud Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(tokens.action.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = "Sunny",
                        tint = tokens.action,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            // 3 Weather Metric Badges (Humidity, Wind, Air Quality)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeatherMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "Humidity",
                    value = "38%",
                    icon = Icons.Rounded.WaterDrop
                )

                WeatherMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "Wind",
                    value = "12 km/h",
                    icon = Icons.Rounded.Air
                )

                WeatherMetricItem(
                    modifier = Modifier.weight(1f),
                    label = "UV Index",
                    value = "4 Low",
                    icon = Icons.Rounded.Thermostat
                )
            }

            // Hourly glass pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HourlyWeatherPill(modifier = Modifier.weight(1f), time = "Now", temp = "28°", isNow = true)
                HourlyWeatherPill(modifier = Modifier.weight(1f), time = "14:00", temp = "31°")
                HourlyWeatherPill(modifier = Modifier.weight(1f), time = "17:00", temp = "29°")
                HourlyWeatherPill(modifier = Modifier.weight(1f), time = "20:00", temp = "26°")
            }
        }
    }
}

@Composable
private fun WeatherMetricItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val tokens = LocalGlassTheme.current

    GlassBox(
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = 2.dp,
        glassAlpha = 0.10f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tokens.action,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(text = value, color = tokens.content, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = label, color = tokens.contentSubtle, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun HourlyWeatherPill(
    modifier: Modifier = Modifier,
    time: String,
    temp: String,
    isNow: Boolean = false
) {
    val tokens = LocalGlassTheme.current

    GlassBox(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = if (isNow) 4.dp else 1.dp,
        glassAlpha = if (isNow) 0.30f else 0.08f,
        borderAlpha = if (isNow) 0.85f else 0.25f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = time,
                color = if (isNow) tokens.action else tokens.contentSubtle,
                fontSize = 11.sp,
                fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = temp,
                color = tokens.content,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
