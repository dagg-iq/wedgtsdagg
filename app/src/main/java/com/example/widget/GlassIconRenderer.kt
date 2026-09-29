package com.example.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import java.util.Calendar

enum class GlassIconStyle {
    IOS_LIQUID_GLASS,   // Translucent frosted glass with white/colored glyph & glass sheen (like iOS 18/VisionOS)
    OBSIDIAN_DARK,      // Deep frosted charcoal glass tile with glowing border
    NEON_CYAN,          // Cyber glass with cyan glowing edge
    ROYAL_PURPLE,       // Purple frosted glass
    EMERALD_GREEN,      // Emerald translucent glass
    APP_LAYERED         // Original App Icon embedded inside a 3D glass squircle capsule
}

object GlassIconRenderer {

    /**
     * Renders an iOS 18 / VisionOS Style Frosted Glass Icon for any installed App
     * Wraps the original app icon inside a luxury glass squircle tile with reflections,
     * shadows, and glossy glass bevels.
     */
    fun renderGlassAppIcon(
        context: Context,
        appDrawable: Drawable?,
        appName: String,
        style: GlassIconStyle = GlassIconStyle.IOS_LIQUID_GLASS,
        customTint: Int? = null,
        size: Int = 256
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }

        val pad = size * 0.08f
        val tileSize = size - (pad * 2f)
        val cornerR = tileSize * 0.23f
        val rect = RectF(pad, pad, pad + tileSize, pad + tileSize)

        // 1. Soft Ambient Drop Shadow beneath the glass tile
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#44000000")
        val shadowRect = RectF(rect.left + 2f, rect.top + 6f, rect.right + 2f, rect.bottom + 10f)
        canvas.drawRoundRect(shadowRect, cornerR, cornerR, paint)

        // 2. Base Frosted Glass Fill
        applyGlassBackdrop(paint, rect, style, customTint)
        canvas.drawRoundRect(rect, cornerR, cornerR, paint)

        // 3. Subtle Inner Gradient Depth (Darker at bottom, luminous at top)
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.left, rect.bottom,
            intArrayOf(Color.parseColor("#26FFFFFF"), Color.TRANSPARENT, Color.parseColor("#1A000000")),
            floatArrayOf(0f, 0.4f, 1.0f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, cornerR, cornerR, paint)

        // 4. Draw Inner App Icon / Content
        if (appDrawable != null) {
            val iconPad = tileSize * 0.22f
            val iconRect = RectF(rect.left + iconPad, rect.top + iconPad, rect.right - iconPad, rect.bottom - iconPad)

            try {
                val origBitmap = try {
                    appDrawable.toBitmap(iconRect.width().toInt().coerceAtLeast(32), iconRect.height().toInt().coerceAtLeast(32))
                } catch (_: Exception) {
                    val b = Bitmap.createBitmap(iconRect.width().toInt().coerceAtLeast(32), iconRect.height().toInt().coerceAtLeast(32), Bitmap.Config.ARGB_8888)
                    val c = Canvas(b)
                    appDrawable.setBounds(0, 0, c.width, c.height)
                    appDrawable.draw(c)
                    b
                }

                // Mask app icon inside squircle with rounded corners
                val innerR = cornerR * 0.65f
                val maskedBitmap = Bitmap.createBitmap(origBitmap.width, origBitmap.height, Bitmap.Config.ARGB_8888)
                val maskCanvas = Canvas(maskedBitmap)
                val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                val mRect = RectF(0f, 0f, origBitmap.width.toFloat(), origBitmap.height.toFloat())
                maskCanvas.drawRoundRect(mRect, innerR, innerR, maskPaint)
                maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                maskCanvas.drawBitmap(origBitmap, 0f, 0f, maskPaint)

                // Draw subtle shadow for inner icon
                paint.shader = null
                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#33000000")
                canvas.drawRoundRect(RectF(iconRect.left, iconRect.top + 4f, iconRect.right, iconRect.bottom + 4f), innerR, innerR, paint)

                // Draw masked icon
                canvas.drawBitmap(maskedBitmap, null, iconRect, null)
            } catch (_: Exception) {}
        } else {
            // Draw Fallback Monogram
            paint.shader = null
            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE
            paint.textSize = tileSize * 0.45f
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER
            val initial = if (appName.isNotBlank()) appName.take(1).uppercase() else "★"
            canvas.drawText(initial, rect.centerX(), rect.centerY() + (tileSize * 0.16f), paint)
        }

        // 5. Specular 3D Glass Reflection Arc across upper half
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.top + tileSize * 0.55f,
            intArrayOf(Color.parseColor("#59FFFFFF"), Color.parseColor("#15FFFFFF"), Color.TRANSPARENT),
            floatArrayOf(0.0f, 0.45f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val glossPath = Path().apply {
            moveTo(rect.left, rect.top + cornerR)
            quadTo(rect.left, rect.top, rect.left + cornerR, rect.top)
            lineTo(rect.right - cornerR, rect.top)
            quadTo(rect.right, rect.top, rect.right, rect.top + cornerR)
            lineTo(rect.right, rect.top + tileSize * 0.35f)
            cubicTo(
                rect.right - tileSize * 0.2f, rect.top + tileSize * 0.50f,
                rect.left + tileSize * 0.2f, rect.top + tileSize * 0.55f,
                rect.left, rect.top + tileSize * 0.40f
            )
            close()
        }
        canvas.drawPath(glossPath, paint)

        // 6. Beveled Refractive Glass Border (Multi-tone metallic/crystal edge)
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.bottom,
            intArrayOf(
                Color.parseColor("#CCFFFFFF"),
                Color.parseColor("#40FFFFFF"),
                Color.parseColor("#15FFFFFF"),
                Color.parseColor("#80FFFFFF")
            ),
            floatArrayOf(0.0f, 0.35f, 0.70f, 1.0f),
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawRoundRect(rect, cornerR, cornerR, paint)

        return bitmap
    }

    /**
     * Renders Pure iOS-style Frosted Glass Glyph Icon
     * (Phone, Messages, Camera, Photos, Clock, Safari, Settings, WhatsApp, etc.)
     */
    fun renderIosPureGlassIcon(
        iconType: String,
        label: String = "",
        style: GlassIconStyle = GlassIconStyle.IOS_LIQUID_GLASS,
        customTint: Int? = null,
        size: Int = 256
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }

        val pad = size * 0.08f
        val tileSize = size - (pad * 2f)
        val cornerR = tileSize * 0.23f
        val rect = RectF(pad, pad, pad + tileSize, pad + tileSize)

        // 1. Drop shadow
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#44000000")
        canvas.drawRoundRect(RectF(rect.left + 2, rect.top + 6, rect.right + 2, rect.bottom + 10), cornerR, cornerR, paint)

        // 2. Base Frosted Glass Fill
        applyGlassBackdrop(paint, rect, style, customTint)
        canvas.drawRoundRect(rect, cornerR, cornerR, paint)

        // 3. Draw Signature iOS Vector Glyph
        drawIosGlyph(canvas, rect, iconType, paint)

        // 4. 3D Specular Glass Glare
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.top + tileSize * 0.55f,
            intArrayOf(Color.parseColor("#55FFFFFF"), Color.parseColor("#12FFFFFF"), Color.TRANSPARENT),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )
        val glossPath = Path().apply {
            moveTo(rect.left, rect.top + cornerR)
            quadTo(rect.left, rect.top, rect.left + cornerR, rect.top)
            lineTo(rect.right - cornerR, rect.top)
            quadTo(rect.right, rect.top, rect.right, rect.top + cornerR)
            lineTo(rect.right, rect.top + tileSize * 0.35f)
            cubicTo(
                rect.right - tileSize * 0.2f, rect.top + tileSize * 0.50f,
                rect.left + tileSize * 0.2f, rect.top + tileSize * 0.55f,
                rect.left, rect.top + tileSize * 0.40f
            )
            close()
        }
        canvas.drawPath(glossPath, paint)

        // 5. Beveled Refractive Glass Border
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.bottom,
            intArrayOf(
                Color.parseColor("#E6FFFFFF"),
                Color.parseColor("#40FFFFFF"),
                Color.parseColor("#1AFFFFFF"),
                Color.parseColor("#99FFFFFF")
            ),
            floatArrayOf(0.0f, 0.35f, 0.70f, 1.0f),
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawRoundRect(rect, cornerR, cornerR, paint)

        return bitmap
    }

    private fun applyGlassBackdrop(paint: Paint, rect: RectF, style: GlassIconStyle, customTint: Int?) {
        paint.style = Paint.Style.FILL

        if (customTint != null) {
            paint.shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(customTint or Color.parseColor("#66000000"), customTint or Color.parseColor("#99000000")),
                null, Shader.TileMode.CLAMP
            )
            return
        }

        when (style) {
            GlassIconStyle.IOS_LIQUID_GLASS -> {
                // High-translucency iOS 18 Vision Glass with subtle gradient
                paint.shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(
                        Color.parseColor("#D9233044"),
                        Color.parseColor("#B3182232"),
                        Color.parseColor("#E60D1522")
                    ),
                    floatArrayOf(0.0f, 0.5f, 1.0f),
                    Shader.TileMode.CLAMP
                )
            }
            GlassIconStyle.OBSIDIAN_DARK -> {
                paint.shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(Color.parseColor("#F215181E"), Color.parseColor("#F70C0D10")),
                    null, Shader.TileMode.CLAMP
                )
            }
            GlassIconStyle.NEON_CYAN -> {
                paint.shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(Color.parseColor("#E60A253A"), Color.parseColor("#D9041422"), Color.parseColor("#F0082E47")),
                    null, Shader.TileMode.CLAMP
                )
            }
            GlassIconStyle.ROYAL_PURPLE -> {
                paint.shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(Color.parseColor("#E62E124A"), Color.parseColor("#D91B072D"), Color.parseColor("#F038175A")),
                    null, Shader.TileMode.CLAMP
                )
            }
            GlassIconStyle.EMERALD_GREEN -> {
                paint.shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(Color.parseColor("#E60C3322"), Color.parseColor("#D9062015"), Color.parseColor("#F010422B")),
                    null, Shader.TileMode.CLAMP
                )
            }
            GlassIconStyle.APP_LAYERED -> {
                paint.shader = LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    intArrayOf(Color.parseColor("#E61E2530"), Color.parseColor("#CC11161E")),
                    null, Shader.TileMode.CLAMP
                )
            }
        }
    }

    private fun drawIosGlyph(canvas: Canvas, rect: RectF, iconType: String, paint: Paint) {
        val cx = rect.centerX()
        val cy = rect.centerY()
        val sz = rect.width()

        paint.shader = null
        paint.color = Color.WHITE

        when (iconType.lowercase()) {
            "phone", "call", "هاتف" -> {
                // Phone Handset
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = sz * 0.12f
                paint.strokeCap = Paint.Cap.ROUND
                val phoneR = sz * 0.22f
                canvas.drawArc(RectF(cx - phoneR, cy - phoneR, cx + phoneR, cy + phoneR), 115f, 60f, false, paint)
                // Earpiece & mic dots
                paint.style = Paint.Style.FILL
                canvas.drawCircle(cx - sz * 0.16f, cy - sz * 0.08f, sz * 0.06f, paint)
                canvas.drawCircle(cx + sz * 0.08f, cy + sz * 0.16f, sz * 0.06f, paint)
            }
            "camera", "كاميرا" -> {
                // Camera Body
                paint.style = Paint.Style.FILL
                val cw = sz * 0.52f
                val ch = sz * 0.38f
                canvas.drawRoundRect(RectF(cx - cw / 2, cy - ch / 2 + 5, cx + cw / 2, cy + ch / 2 + 5), 16f, 16f, paint)
                // Top bump
                canvas.drawRoundRect(RectF(cx - cw * 0.25f, cy - ch / 2 - 6, cx + cw * 0.25f, cy - ch / 2 + 6), 6f, 6f, paint)
                // Lens (outer ring + center dot)
                paint.color = Color.parseColor("#FF151C26")
                canvas.drawCircle(cx, cy + 5, sz * 0.13f, paint)
                paint.color = Color.WHITE
                canvas.drawCircle(cx, cy + 5, sz * 0.06f, paint)
            }
            "photos", "gallery", "معرض" -> {
                // iOS Photos Flower Petals
                val petalR = sz * 0.10f
                val colors = intArrayOf(
                    Color.parseColor("#FFFF3B30"),
                    Color.parseColor("#FFFF9500"),
                    Color.parseColor("#FFFFCC00"),
                    Color.parseColor("#FF34C759"),
                    Color.parseColor("#FF007AFF"),
                    Color.parseColor("#FF5856D6"),
                    Color.parseColor("#FFAF52DE"),
                    Color.parseColor("#FFFF2D55")
                )
                paint.style = Paint.Style.FILL
                for (i in 0 until 8) {
                    val angleRad = Math.toRadians((i * 45).toDouble())
                    val px = cx + (sz * 0.14f * Math.cos(angleRad)).toFloat()
                    val py = cy + (sz * 0.14f * Math.sin(angleRad)).toFloat()
                    paint.color = colors[i]
                    canvas.drawCircle(px, py, petalR, paint)
                }
                // Center white core
                paint.color = Color.WHITE
                canvas.drawCircle(cx, cy, petalR * 0.8f, paint)
            }
            "calendar", "تقويم" -> {
                // Calendar Page
                val calW = sz * 0.54f
                val calH = sz * 0.54f
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                canvas.drawRoundRect(RectF(cx - calW / 2, cy - calH / 2, cx + calW / 2, cy + calH / 2), 16f, 16f, paint)
                // Red Top Header Bar
                paint.color = Color.parseColor("#FFFF3B30")
                canvas.drawRoundRect(RectF(cx - calW / 2, cy - calH / 2, cx + calW / 2, cy - calH / 2 + calH * 0.30f), 12f, 12f, paint)
                // Day number text
                val dayStr = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
                paint.color = Color.parseColor("#FF151C26")
                paint.textSize = sz * 0.32f
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(dayStr, cx, cy + sz * 0.16f, paint)
            }
            "clock", "ساعة" -> {
                // Clock Dial
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                paint.color = Color.WHITE
                val cr = sz * 0.28f
                canvas.drawCircle(cx, cy, cr, paint)
                // Hands
                val cal = Calendar.getInstance()
                val h = cal.get(Calendar.HOUR)
                val m = cal.get(Calendar.MINUTE)
                val hAngle = Math.toRadians(((h % 12 + m / 60f) / 12f * 360f - 90).toDouble())
                val mAngle = Math.toRadians(((m / 60f) * 360f - 90).toDouble())

                paint.strokeWidth = 4f
                canvas.drawLine(cx, cy, cx + (cr * 0.55f * Math.cos(hAngle)).toFloat(), cy + (cr * 0.55f * Math.sin(hAngle)).toFloat(), paint)
                paint.strokeWidth = 3f
                canvas.drawLine(cx, cy, cx + (cr * 0.78f * Math.cos(mAngle)).toFloat(), cy + (cr * 0.78f * Math.sin(mAngle)).toFloat(), paint)
                // Center pinion
                paint.style = Paint.Style.FILL
                canvas.drawCircle(cx, cy, 5f, paint)
            }
            "mail", "بريد" -> {
                // Envelope
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5f
                val mw = sz * 0.50f
                val mh = sz * 0.34f
                canvas.drawRoundRect(RectF(cx - mw / 2, cy - mh / 2, cx + mw / 2, cy + mh / 2), 10f, 10f, paint)
                // Flap lines
                val path = Path().apply {
                    moveTo(cx - mw / 2 + 4, cy - mh / 2 + 4)
                    lineTo(cx, cy + 4)
                    lineTo(cx + mw / 2 - 4, cy - mh / 2 + 4)
                }
                canvas.drawPath(path, paint)
            }
            "messages", "chat", "رسائل" -> {
                // Speech Bubble
                paint.style = Paint.Style.FILL
                val bw = sz * 0.52f
                val bh = sz * 0.38f
                canvas.drawRoundRect(RectF(cx - bw / 2, cy - bh / 2 - 3, cx + bw / 2, cy + bh / 2 - 3), 20f, 20f, paint)
                // Tail
                val tail = Path().apply {
                    moveTo(cx - bw * 0.25f, cy + bh / 2 - 5)
                    lineTo(cx - bw * 0.35f, cy + bh / 2 + 14)
                    lineTo(cx - bw * 0.05f, cy + bh / 2 - 5)
                    close()
                }
                canvas.drawPath(tail, paint)
                // 3 Dots inside
                paint.color = Color.parseColor("#FF151C26")
                canvas.drawCircle(cx - sz * 0.12f, cy - 3, 5f, paint)
                canvas.drawCircle(cx, cy - 3, 5f, paint)
                canvas.drawCircle(cx + sz * 0.12f, cy - 3, 5f, paint)
            }
            "whatsapp", "واتساب" -> {
                // WhatsApp Speech Bubble + Phone
                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#FF25D366")
                val bw = sz * 0.54f
                val bh = sz * 0.42f
                canvas.drawRoundRect(RectF(cx - bw / 2, cy - bh / 2 - 2, cx + bw / 2, cy + bh / 2 - 2), 22f, 22f, paint)
                val tail = Path().apply {
                    moveTo(cx - bw * 0.25f, cy + bh / 2 - 4)
                    lineTo(cx - bw * 0.38f, cy + bh / 2 + 15)
                    lineTo(cx - bw * 0.05f, cy + bh / 2 - 4)
                    close()
                }
                canvas.drawPath(tail, paint)
                // White handset inside
                paint.color = Color.WHITE
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 6f
                paint.strokeCap = Paint.Cap.ROUND
                canvas.drawArc(RectF(cx - 18, cy - 18, cx + 18, cy + 18), 115f, 60f, false, paint)
            }
            "settings", "إعدادات" -> {
                // Gear
                paint.style = Paint.Style.FILL
                canvas.drawCircle(cx, cy, sz * 0.16f, paint)
                for (k in 0 until 6) {
                    val a = Math.toRadians((k * 60).toDouble())
                    val gx = cx + (sz * 0.22f * Math.cos(a)).toFloat()
                    val gy = cy + (sz * 0.22f * Math.sin(a)).toFloat()
                    canvas.drawCircle(gx, gy, sz * 0.07f, paint)
                }
                // Center hole
                paint.color = Color.parseColor("#FF151C26")
                canvas.drawCircle(cx, cy, sz * 0.08f, paint)
            }
            "music", "موسيقى" -> {
                // Double Eighth Note
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                canvas.drawCircle(cx - sz * 0.10f, cy + sz * 0.12f, sz * 0.09f, paint)
                canvas.drawCircle(cx + sz * 0.12f, cy + sz * 0.04f, sz * 0.09f, paint)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5f
                canvas.drawLine(cx - sz * 0.03f, cy + sz * 0.10f, cx - sz * 0.03f, cy - sz * 0.16f, paint)
                canvas.drawLine(cx + sz * 0.19f, cy + sz * 0.02f, cx + sz * 0.19f, cy - sz * 0.24f, paint)
                paint.strokeWidth = 10f
                canvas.drawLine(cx - sz * 0.03f, cy - sz * 0.16f, cx + sz * 0.20f, cy - sz * 0.24f, paint)
            }
            "browser", "chrome", "safari", "متصفح" -> {
                // Compass / Globe
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                val globeR = sz * 0.26f
                canvas.drawCircle(cx, cy, globeR, paint)
                // Needle
                paint.style = Paint.Style.FILL
                val needle = Path().apply {
                    moveTo(cx - 6, cy)
                    lineTo(cx + globeR * 0.70f, cy - globeR * 0.70f)
                    lineTo(cx, cy + 6)
                    close()
                }
                paint.color = Color.parseColor("#FFFF3B30")
                canvas.drawPath(needle, paint)
                val needle2 = Path().apply {
                    moveTo(cx - 6, cy)
                    lineTo(cx - globeR * 0.70f, cy + globeR * 0.70f)
                    lineTo(cx, cy + 6)
                    close()
                }
                paint.color = Color.WHITE
                canvas.drawPath(needle2, paint)
            }
            else -> {
                // Generic Glass Monogram / Star
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                paint.textSize = sz * 0.40f
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textAlign = Paint.Align.CENTER
                val initial = if (iconType.isNotBlank()) iconType.take(1).uppercase() else "★"
                canvas.drawText(initial, cx, cy + sz * 0.14f, paint)
            }
        }
    }

    /**
     * Renders a 4-App or 8-App iOS Frosted Glass Dock Widget (720x200 or 720x360)
     */
    fun renderGlassDockWidget(
        context: Context,
        appIcons: List<Bitmap>,
        appLabels: List<String>,
        width: Int = 720,
        height: Int = 200
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }

        val rect = RectF(12f, 12f, width - 12f, height - 12f)
        val pillR = 48f

        // 1. Frosted Glass Ambient Dock Base (Translucent iOS dock)
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.parseColor("#80FFFFFF"),
                Color.parseColor("#4DFFFFFF"),
                Color.parseColor("#26FFFFFF")
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, pillR, pillR, paint)

        // 2. Beveled Glass Border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.bottom,
            intArrayOf(Color.parseColor("#CCFFFFFF"), Color.parseColor("#33FFFFFF"), Color.parseColor("#80FFFFFF")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, pillR, pillR, paint)

        // 3. Draw 4 App Icons evenly spaced
        val count = appIcons.size.coerceAtMost(4)
        if (count > 0) {
            val step = width.toFloat() / count
            for (i in 0 until count) {
                val icx = (i * step) + (step / 2f)
                val icy = height / 2f - 8f
                val icSize = 110f
                val dst = RectF(icx - icSize / 2f, icy - icSize / 2f, icx + icSize / 2f, icy + icSize / 2f)

                canvas.drawBitmap(appIcons[i], null, dst, null)

                // Optional label beneath icon
                if (i < appLabels.size && appLabels[i].isNotBlank()) {
                    paint.shader = null
                    paint.style = Paint.Style.FILL
                    paint.color = Color.WHITE
                    paint.textSize = 20f
                    paint.typeface = Typeface.DEFAULT_BOLD
                    paint.textAlign = Paint.Align.CENTER
                    val label = if (appLabels[i].length > 8) appLabels[i].take(7) + "…" else appLabels[i]
                    canvas.drawText(label, icx, icy + icSize / 2f + 24f, paint)
                }
            }
        }

        return bitmap
    }
}
