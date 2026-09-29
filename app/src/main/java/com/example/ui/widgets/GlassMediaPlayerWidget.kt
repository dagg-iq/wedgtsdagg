package com.example.ui.widgets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.LocalGlassTheme

@Composable
fun GlassMediaPlayerWidget(
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    var isPlaying by remember { mutableStateOf(true) }
    var isLiked by remember { mutableStateOf(true) }
    var trackProgress by remember { mutableFloatStateOf(0.42f) }

    val infiniteTransition = rememberInfiniteTransition(label = "audio_visualizer")
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(560, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar3"
    )
    val bar4 by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar4"
    )

    GlassBox(
        modifier = modifier
            .fillMaxWidth()
            .testTag("glass_media_player"),
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
            // Track Info & Vinyl Art
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Glass Frosted Album Art
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    tokens.action,
                                    tokens.dark.copy(alpha = 0.85f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "Cover",
                        tint = tokens.light,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Title and Artist
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reflections of Glass",
                        color = tokens.content,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Aetheria Sounds • 2026",
                        color = tokens.contentSubtle,
                        fontSize = 13.sp
                    )
                }

                // Like heart button
                GlassBox(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    elevation = 2.dp,
                    glassAlpha = 0.12f,
                    onClick = { isLiked = !isLiked }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) tokens.action else tokens.contentSubtle,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Audio Equalizer & Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Audio Wave Visualizer Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf(bar1, bar2, bar3, bar4, bar2, bar1, bar3).forEach { barHeight ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(if (isPlaying) barHeight else 0.15f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(tokens.action.copy(alpha = 0.65f))
                        )
                    }
                }

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(tokens.light.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(trackProgress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(tokens.action)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "1:42", color = tokens.contentSubtle, fontSize = 11.sp)
                    Text(text = "3:58", color = tokens.contentSubtle, fontSize = 11.sp)
                }
            }

            // Playback controls in Glass Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassBox(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    elevation = 3.dp,
                    glassAlpha = 0.12f,
                    onClick = { trackProgress = (trackProgress - 0.1f).coerceAtLeast(0f) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = tokens.content,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Big Play/Pause Button
                GlassBox(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    elevation = 8.dp,
                    glassAlpha = 0.35f,
                    borderAlpha = 0.9f,
                    onClick = { isPlaying = !isPlaying }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = tokens.action,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                GlassBox(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    elevation = 3.dp,
                    glassAlpha = 0.12f,
                    onClick = { trackProgress = (trackProgress + 0.1f).coerceAtMost(1f) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = tokens.content,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}
