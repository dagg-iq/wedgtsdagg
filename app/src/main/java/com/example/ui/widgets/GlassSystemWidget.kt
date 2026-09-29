package com.example.ui.widgets

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.LocalGlassTheme
import kotlinx.coroutines.delay

@Composable
fun GlassSystemWidget(
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current
    val context = LocalContext.current

    var batteryPercent by remember { mutableIntStateOf(84) }
    var isCharging by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (level >= 0 && scale > 0) {
            batteryPercent = ((level / scale.toFloat()) * 100).toInt()
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_battery")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "wave"
    )

    GlassBox(
        modifier = modifier
            .fillMaxWidth()
            .testTag("glass_system_widget"),
        shape = RoundedCornerShape(26.dp),
        elevation = 10.dp,
        glassAlpha = 0.16f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = "System Health",
                        tint = tokens.action,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "System & Power",
                        color = tokens.content,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Text(
                    text = if (isCharging) "Charging Fast" else "Discharging",
                    color = tokens.action,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Circular Glass Battery Gauge & System Meters
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular Gauge
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 10.dp.toPx()
                        val arcRadius = (size.minDimension - strokeW) / 2f
                        val centerOffset = Offset(size.width / 2f, size.height / 2f)

                        // Track
                        drawCircle(
                            color = tokens.light.copy(alpha = 0.12f),
                            radius = arcRadius,
                            center = centerOffset,
                            style = Stroke(width = strokeW)
                        )

                        // Progress Arc
                        val sweep = (batteryPercent / 100f) * 360f
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to tokens.action.copy(alpha = 0.6f),
                                0.7f to tokens.action,
                                1.0f to tokens.light,
                                center = centerOffset
                            ),
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(centerOffset.x - arcRadius, centerOffset.y - arcRadius),
                            size = Size(arcRadius * 2f, arcRadius * 2f),
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$batteryPercent%",
                            color = tokens.content,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Battery",
                            color = tokens.contentSubtle,
                            fontSize = 10.sp
                        )
                    }
                }

                // RAM & Storage meters
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SystemResourceRow(
                        label = "RAM Memory",
                        used = "3.8 GB / 8 GB",
                        percent = 0.48f,
                        icon = Icons.Rounded.Memory,
                        tokens = tokens
                    )

                    SystemResourceRow(
                        label = "Internal Storage",
                        used = "68 GB / 128 GB",
                        percent = 0.53f,
                        icon = Icons.Rounded.SdStorage,
                        tokens = tokens
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemResourceRow(
    label: String,
    used: String,
    percent: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tokens: com.example.ui.theme.GlassColorTokens
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tokens.action,
                    modifier = Modifier.size(14.dp)
                )
                Text(text = label, color = tokens.content, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(text = used, color = tokens.contentSubtle, fontSize = 10.sp)
        }

        // Frosted Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(tokens.light.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percent)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(tokens.action)
            )
        }
    }
}
