package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.widgets.GlassControlCenterWidget
import com.example.ui.widgets.GlassMediaPlayerWidget
import com.example.ui.widgets.GlassSystemWidget

@Composable
fun GlassControlScreen(
    modifier: Modifier = Modifier
) {
    val tokens = LocalGlassTheme.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "SYSTEM CONTROLS & MEDIA",
                color = tokens.action,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        item {
            GlassControlCenterWidget()
        }

        item {
            GlassMediaPlayerWidget()
        }

        item {
            GlassSystemWidget()
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
