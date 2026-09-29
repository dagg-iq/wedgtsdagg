package com.example.ui.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.GlassColorTokens
import com.example.ui.theme.LocalGlassTheme
import kotlin.math.roundToInt

@Composable
fun GlassControlCenterWidget(
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    var wifiActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(true) }
    var soundActive by remember { mutableStateOf(false) }
    var torchActive by remember { mutableStateOf(false) }
    var dndActive by remember { mutableStateOf(false) }
    var airplaneActive by remember { mutableStateOf(false) }

    var brightnessValue by remember { mutableFloatStateOf(0.75f) }
    var volumeValue by remember { mutableFloatStateOf(0.60f) }

    GlassBox(
        modifier = modifier
            .fillMaxWidth()
            .testTag("glass_control_center"),
        shape = RoundedCornerShape(28.dp),
        elevation = 10.dp,
        glassAlpha = 0.15f
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
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(tokens.action)
                    )
                    Text(
                        text = "Quick Controls",
                        color = tokens.content,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Text(
                    text = "Connected",
                    color = tokens.action,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 6 Toggle Tiles (3 per row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassToggleTile(
                    modifier = Modifier.weight(1f),
                    title = "Wi-Fi",
                    subtitle = if (wifiActive) "Glass 5G" else "Off",
                    icon = Icons.Rounded.Wifi,
                    isActive = wifiActive,
                    tokens = tokens,
                    onToggle = { wifiActive = !wifiActive }
                )

                GlassToggleTile(
                    modifier = Modifier.weight(1f),
                    title = "Bluetooth",
                    subtitle = if (bluetoothActive) "AirPods" else "Off",
                    icon = Icons.Rounded.Bluetooth,
                    isActive = bluetoothActive,
                    tokens = tokens,
                    onToggle = { bluetoothActive = !bluetoothActive }
                )

                GlassToggleTile(
                    modifier = Modifier.weight(1f),
                    title = "Audio",
                    subtitle = if (soundActive) "Mute" else "Ring",
                    icon = Icons.Rounded.VolumeUp,
                    isActive = !soundActive,
                    tokens = tokens,
                    onToggle = { soundActive = !soundActive }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassToggleTile(
                    modifier = Modifier.weight(1f),
                    title = "Torch",
                    subtitle = if (torchActive) "On" else "Off",
                    icon = Icons.Rounded.FlashlightOn,
                    isActive = torchActive,
                    tokens = tokens,
                    onToggle = { torchActive = !torchActive }
                )

                GlassToggleTile(
                    modifier = Modifier.weight(1f),
                    title = "DND",
                    subtitle = if (dndActive) "Active" else "Off",
                    icon = Icons.Rounded.DoNotDisturbOn,
                    isActive = dndActive,
                    tokens = tokens,
                    onToggle = { dndActive = !dndActive }
                )

                GlassToggleTile(
                    modifier = Modifier.weight(1f),
                    title = "Flight",
                    subtitle = if (airplaneActive) "On" else "Off",
                    icon = Icons.Rounded.AirplanemodeActive,
                    isActive = airplaneActive,
                    tokens = tokens,
                    onToggle = { airplaneActive = !airplaneActive }
                )
            }

            // 2 Glass Sliders (Brightness and Volume)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassVerticalSlider(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp),
                    value = brightnessValue,
                    onValueChange = { brightnessValue = it },
                    icon = Icons.Rounded.BrightnessMedium,
                    label = "Display",
                    tokens = tokens
                )

                GlassVerticalSlider(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp),
                    value = volumeValue,
                    onValueChange = { volumeValue = it },
                    icon = Icons.Rounded.VolumeUp,
                    label = "Volume",
                    tokens = tokens
                )
            }
        }
    }
}

@Composable
private fun GlassToggleTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    tokens: GlassColorTokens,
    onToggle: () -> Unit
) {
    val lightReflex = tokens.glassReflexLight
    val darkReflex = tokens.glassReflexDark

    val bgAlpha = if (isActive) 0.35f else 0.10f
    val iconTint = if (isActive) tokens.action else tokens.content.copy(alpha = 0.6f)
    val animatedBgAlpha by animateFloatAsState(bgAlpha, label = "bg_alpha")

    GlassBox(
        modifier = modifier
            .height(78.dp)
            .testTag("toggle_$title"),
        shape = RoundedCornerShape(18.dp),
        elevation = if (isActive) 5.dp else 2.dp,
        glassAlpha = animatedBgAlpha,
        borderAlpha = if (isActive) 0.85f else 0.30f,
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Column {
                Text(
                    text = title,
                    color = tokens.content,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = tokens.contentSubtle,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun GlassVerticalSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    label: String,
    tokens: GlassColorTokens
) {
    val lightReflex = tokens.glassReflexLight
    val darkReflex = tokens.glassReflexDark

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tokens.light.copy(alpha = (0.16f * lightReflex).coerceIn(0.05f, 0.4f)),
                        tokens.glass.copy(alpha = 0.12f),
                        tokens.dark.copy(alpha = (0.15f * darkReflex).coerceIn(0.04f, 0.35f))
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        tokens.light.copy(alpha = (0.7f * lightReflex).coerceIn(0.15f, 0.95f)),
                        tokens.light.copy(alpha = 0.2f),
                        tokens.dark.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val ratio = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                        onValueChange(ratio)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val ratio = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                        onValueChange(ratio)
                    }
                )
            }
    ) {
        // Fluid Frosted Level Fill from bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(value.coerceIn(0f, 1f))
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tokens.action.copy(alpha = 0.65f),
                            tokens.action.copy(alpha = 0.35f)
                        )
                    )
                )
                .drawBehind {
                    // Glowing top liquid surface line
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                tokens.light.copy(alpha = 0.9f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2.5f
                    )
                }
        )

        // Icon & Percentage Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(value * 100).roundToInt()}%",
                color = tokens.content,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tokens.content,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    color = tokens.contentSubtle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
