package com.example.ui.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGlassTheme

enum class GlassNavTab(val title: String, val icon: ImageVector) {
    SHOWCASE("Widgets", Icons.Rounded.Widgets),
    ICONS("Icons", Icons.Rounded.AutoAwesome),
    CLOCK("Horology", Icons.Rounded.AccessTime),
    CONTROLS("Controls", Icons.Rounded.Tune),
    CUSTOMIZER("Studio", Icons.Rounded.Palette)
}

@Composable
fun GlassBottomNavigation(
    selectedTab: GlassNavTab,
    onTabSelected: (GlassNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .testTag("glass_bottom_navigation"),
            shape = CircleShape,
            elevation = 14.dp,
            glassAlpha = 0.22f,
            borderAlpha = 0.85f
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassNavTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val tintColor by animateColorAsState(
                        targetValue = if (isSelected) tokens.action else tokens.contentSubtle,
                        animationSpec = spring(),
                        label = "tab_tint"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(tab) }
                            )
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(width = 54.dp, height = 34.dp)
                                    .clip(CircleShape)
                                    .background(tokens.action.copy(alpha = 0.18f))
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = tintColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tab.title,
                                color = tintColor,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
