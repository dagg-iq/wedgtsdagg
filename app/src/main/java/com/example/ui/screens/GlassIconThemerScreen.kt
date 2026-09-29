package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddHome
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassBox
import com.example.ui.theme.LocalGlassTheme
import com.example.widget.GlassDockWidgetProvider
import com.example.widget.GlassIconRenderer
import com.example.widget.GlassIconStyle
import com.example.widget.GlassShortcutHelper
import com.example.widget.InstalledAppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class IosPresetItem(
    val id: String,
    val name: String,
    val category: String,
    val defaultPackage: String
)

@Composable
fun GlassIconThemerScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokens = LocalGlassTheme.current

    var selectedStyle by remember { mutableStateOf(GlassIconStyle.IOS_LIQUID_GLASS) }
    var searchQuery by remember { mutableStateOf("") }
    var installedApps by remember { mutableStateOf<List<InstalledAppItem>>(emptyList()) }
    var isLoadingApps by remember { mutableStateOf(true) }

    // Load installed apps in background
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val apps = GlassShortcutHelper.getInstalledApps(context)
            installedApps = apps
            isLoadingApps = false
        }
    }

    val iosPresets = remember {
        listOf(
            IosPresetItem("phone", "الهاتف", "اتصال", "com.android.dialer"),
            IosPresetItem("messages", "الرسائل", "محادثة", "com.android.mms"),
            IosPresetItem("camera", "الكاميرا", "تصوير", "com.android.camera"),
            IosPresetItem("photos", "المعرض", "صور", "com.android.gallery3d"),
            IosPresetItem("clock", "الساعة", "وقت", "com.android.deskclock"),
            IosPresetItem("calendar", "التقويم", "مواعيد", "com.android.calendar"),
            IosPresetItem("browser", "كروم / سفاري", "تصفح", "com.android.chrome"),
            IosPresetItem("whatsapp", "واتساب", "تواصل", "com.whatsapp"),
            IosPresetItem("mail", "البريد", "إيميل", "com.google.android.gm"),
            IosPresetItem("music", "الموسيقى", "صوتيات", "com.google.android.apps.youtube.music"),
            IosPresetItem("settings", "الإعدادات", "نظام", "com.android.settings")
        )
    }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter { it.appName.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Header Title Card
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                elevation = 10.dp,
                glassAlpha = 0.18f,
                borderAlpha = 0.80f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = tokens.action,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "ثيم أيقونات iOS الزجاجي الفاخر",
                            color = tokens.content,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "إضافة طبقات زجاجية حقيقية وانعكاسات كرونوغراف فوق أيقونات تطبيقات هاتفك مثل نظام iOS 18 و VisionOS مباشرة على الشاشة الرئيسية",
                        color = tokens.contentSubtle,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Section 1: iOS Glass Dock Widget Card
        item {
            GlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = 12.dp,
                glassAlpha = 0.22f,
                borderAlpha = 0.85f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "شريط تطبيقات iOS الزجاجي (Dock Widget)",
                            color = tokens.content,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(tokens.action.copy(alpha = 0.20f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "ويدجت تفاعلي 4x1",
                                color = tokens.action,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Live Dock Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("phone", "messages", "browser", "camera").forEach { type ->
                                val bmp = remember(type, selectedStyle) {
                                    GlassIconRenderer.renderIosPureGlassIcon(type, type, selectedStyle, null, 128)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = type,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when(type) {
                                            "phone" -> "هاتف"
                                            "messages" -> "رسائل"
                                            "browser" -> "تصفح"
                                            else -> "كاميرا"
                                        },
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Button: Pin Dock Widget to Home Screen
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val appWidgetManager = AppWidgetManager.getInstance(context)
                                val provider = ComponentName(context, GlassDockWidgetProvider::class.java)
                                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                    appWidgetManager.requestPinAppWidget(provider, null, null)
                                } else {
                                    Toast.makeText(context, "يرجى إضافة الويدجت من قائمة ويدجات الهاتف", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "اسحب الويدجت من قائمة الشاشة الرئيسية", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = tokens.action),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddHome,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إضافة شريط التطبيقات الزجاجي إلى الشاشة الرئيسية",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Section 2: Choose Glass Style
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "اختر ستايل طبقة الزجاج (Glass Style):",
                    color = tokens.content,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    val styles = listOf(
                        Triple(GlassIconStyle.IOS_LIQUID_GLASS, "زجاج iOS الشفاف", "Liquid Glass"),
                        Triple(GlassIconStyle.APP_LAYERED, "تغليف أيقونة التطبيق", "App Wrap"),
                        Triple(GlassIconStyle.OBSIDIAN_DARK, "زجاج فاحم داكن", "Obsidian Dark"),
                        Triple(GlassIconStyle.NEON_CYAN, "نيون سايبر مشع", "Neon Cyan"),
                        Triple(GlassIconStyle.ROYAL_PURPLE, "أرجواني ملكي", "Royal Purple"),
                        Triple(GlassIconStyle.EMERALD_GREEN, "زمردي إميرالد", "Cyber Emerald")
                    )

                    items(styles) { (style, arName, enName) ->
                        val isSelected = selectedStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) tokens.action.copy(alpha = 0.28f)
                                    else tokens.light.copy(alpha = 0.08f)
                                )
                                .border(
                                    1.5.dp,
                                    if (isSelected) tokens.action else tokens.light.copy(alpha = 0.15f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedStyle = style }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = arName,
                                    color = if (isSelected) tokens.content else tokens.contentSubtle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = enName,
                                    color = if (isSelected) tokens.action else tokens.contentSubtle.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Preset iOS Pure Glass Icons
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أيقونات زجاجية جاهزة (اضغط للإضافة مباشرة):",
                        color = tokens.content,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(iosPresets) { preset ->
                        val bmp = remember(preset.id, selectedStyle) {
                            GlassIconRenderer.renderIosPureGlassIcon(preset.id, preset.name, selectedStyle, null, 192)
                        }

                        GlassBox(
                            modifier = Modifier
                                .width(94.dp)
                                .clickable {
                                    // Match installed package or fallback
                                    val targetPkg = installedApps.firstOrNull {
                                        it.appName.contains(preset.name, ignoreCase = true) ||
                                        it.packageName.contains(preset.id, ignoreCase = true)
                                    }?.packageName ?: preset.defaultPackage

                                    val ok = GlassShortcutHelper.pinAppShortcut(context, targetPkg, preset.name, bmp)
                                },
                            shape = RoundedCornerShape(18.dp),
                            elevation = 4.dp,
                            glassAlpha = 0.14f
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = preset.name,
                                    modifier = Modifier.size(56.dp)
                                )
                                Text(
                                    text = preset.name,
                                    color = tokens.content,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(tokens.action.copy(alpha = 0.20f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+ إضافة",
                                        color = tokens.action,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: All Installed Apps on User Phone
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تطبيق التأثير الزجاجي على تطبيقات هاتفك:",
                    color = tokens.content,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن أي تطبيق مثبت على هاتفك...", color = tokens.contentSubtle, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = tokens.action) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.action,
                        unfocusedBorderColor = tokens.light.copy(alpha = 0.25f),
                        focusedTextColor = tokens.content,
                        unfocusedTextColor = tokens.content
                    ),
                    singleLine = true
                )
            }
        }

        // Apps List
        if (isLoadingApps) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = tokens.action)
                }
            }
        } else {
            items(filteredApps.take(30)) { app ->
                val glassBmp = remember(app.packageName, selectedStyle) {
                    GlassIconRenderer.renderGlassAppIcon(
                        context = context,
                        appDrawable = app.iconDrawable,
                        appName = app.appName,
                        style = selectedStyle,
                        size = 192
                    )
                }

                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = 3.dp,
                    glassAlpha = 0.12f
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            bitmap = glassBmp.asImageBitmap(),
                            contentDescription = app.appName,
                            modifier = Modifier.size(54.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.appName,
                                color = tokens.content,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = app.packageName,
                                color = tokens.contentSubtle,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        // Add to Home Screen Action Button
                        Button(
                            onClick = {
                                GlassShortcutHelper.pinAppShortcut(
                                    context = context,
                                    packageName = app.packageName,
                                    appName = app.appName,
                                    glassIconBitmap = glassBmp
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.action),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddHome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "إضافة للشاشة",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
