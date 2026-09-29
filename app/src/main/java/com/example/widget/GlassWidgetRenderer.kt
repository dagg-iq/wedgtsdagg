package com.example.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

object GlassWidgetRenderer {

    // MARK: - Luxury Analog Horology Clock (600x600 Bitmap)
    fun renderLuxuryClock(context: Context, width: Int = 600, height: Int = 600): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val cx = width / 2f
        val cy = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) * 0.92f

        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)

        val hourAngle = ((hour % 12 + minute / 60f) / 12f) * 360f
        val minuteAngle = ((minute + second / 60f) / 60f) * 360f
        val secondAngle = (second / 60f) * 360f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
        }

        // 1. Ambient Glass Outer Shadow & Outer Ring
        paint.style = Paint.Style.FILL
        paint.shader = RadialGradient(
            cx, cy, radius * 1.08f,
            intArrayOf(Color.parseColor("#4003D5FF"), Color.parseColor("#15000000"), Color.TRANSPARENT),
            floatArrayOf(0.85f, 0.98f, 1.0f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius * 1.05f, paint)

        // 2. Beveled Metallic Rim (Multi-stage steel/titanium border)
        paint.shader = LinearGradient(
            cx - radius, cy - radius, cx + radius, cy + radius,
            intArrayOf(
                Color.parseColor("#99FFFFFF"),
                Color.parseColor("#224466"),
                Color.parseColor("#111A24"),
                Color.parseColor("#66FFFFFF")
            ),
            floatArrayOf(0.0f, 0.35f, 0.70f, 1.0f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, paint)

        // 3. Main Frosted Obsidian Glass Dial Face
        val dialRadius = radius * 0.92f
        paint.shader = RadialGradient(
            cx - radius * 0.2f, cy - radius * 0.25f, dialRadius * 1.2f,
            intArrayOf(
                Color.parseColor("#EE1C232E"),
                Color.parseColor("#FA121720"),
                Color.parseColor("#FF0A0D12")
            ),
            floatArrayOf(0.0f, 0.55f, 1.0f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, dialRadius, paint)

        // 4. Subtle Guilloché concentric tracks on dial
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#1AFFFFFF")
        paint.strokeWidth = 1.5f
        canvas.drawCircle(cx, cy, dialRadius * 0.82f, paint)
        canvas.drawCircle(cx, cy, dialRadius * 0.60f, paint)

        // 5. 60 Minute Precision Railway Ticks
        paint.strokeCap = Paint.Cap.ROUND
        for (i in 0 until 60) {
            val angleRad = Math.toRadians((i * 6).toDouble())
            val isMajor = (i % 5 == 0)
            val tickLen = if (isMajor) dialRadius * 0.09f else dialRadius * 0.045f
            val startR = dialRadius * 0.90f - tickLen
            val endR = dialRadius * 0.90f

            paint.color = if (isMajor) Color.parseColor("#CCFFFFFF") else Color.parseColor("#44FFFFFF")
            paint.strokeWidth = if (isMajor) 3.5f else 1.5f

            val sx = cx + (startR * sin(angleRad)).toFloat()
            val sy = cy - (startR * cos(angleRad)).toFloat()
            val ex = cx + (endR * sin(angleRad)).toFloat()
            val ey = cy - (endR * cos(angleRad)).toFloat()
            canvas.drawLine(sx, sy, ex, ey, paint)
        }

        // 6. 12 Luxury Metallic Luminescent Hour Batons
        paint.style = Paint.Style.FILL
        for (i in 1..12) {
            val angleRad = Math.toRadians((i * 30).toDouble())
            val markerR = dialRadius * 0.76f
            val mx = cx + (markerR * sin(angleRad)).toFloat()
            val my = cy - (markerR * cos(angleRad)).toFloat()

            canvas.save()
            canvas.rotate(i * 30f, mx, my)

            // Diamond-cut baton
            val bw = 6f
            val bh = 18f
            paint.color = Color.parseColor("#99A0B0")
            canvas.drawRoundRect(RectF(mx - bw, my - bh, mx + bw, my + bh), 3f, 3f, paint)
            // Luminous glowing cyan core
            paint.color = Color.parseColor("#03D5FF")
            canvas.drawRoundRect(RectF(mx - bw * 0.5f, my - bh * 0.7f, mx + bw * 0.5f, my + bh * 0.7f), 2f, 2f, paint)

            canvas.restore()
        }

        // 7. Sub-dial at 6 o'clock (Chronograph seconds gauge)
        val subY = cy + dialRadius * 0.40f
        val subR = dialRadius * 0.28f
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#40000000")
        canvas.drawCircle(cx, subY, subR, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#3303D5FF")
        paint.strokeWidth = 2f
        canvas.drawCircle(cx, subY, subR, paint)

        // Subdial ticks
        for (k in 0 until 12) {
            val aRad = Math.toRadians((k * 30).toDouble())
            val sR1 = subR * 0.75f
            val sR2 = subR * 0.90f
            paint.color = Color.parseColor("#80FFFFFF")
            paint.strokeWidth = 1.5f
            canvas.drawLine(
                cx + (sR1 * sin(aRad)).toFloat(), subY - (sR1 * cos(aRad)).toFloat(),
                cx + (sR2 * sin(aRad)).toFloat(), subY - (sR2 * cos(aRad)).toFloat(),
                paint
            )
        }
        // Subdial mini hand
        val subAngle = (second / 60f) * 360f
        canvas.save()
        canvas.rotate(subAngle, cx, subY)
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#03D5FF")
        canvas.drawRoundRect(RectF(cx - 2f, subY - subR * 0.75f, cx + 2f, subY + 6f), 2f, 2f, paint)
        canvas.restore()

        // 8. Date Window Complication at 3 o'clock
        val dateX = cx + dialRadius * 0.54f
        val dateY = cy
        val dw = 26f
        val dh = 16f
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#222A36")
        canvas.drawRoundRect(RectF(dateX - dw, dateY - dh, dateX + dw, dateY + dh), 5f, 5f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#66FFFFFF")
        paint.strokeWidth = 1.5f
        canvas.drawRoundRect(RectF(dateX - dw, dateY - dh, dateX + dw, dateY + dh), 5f, 5f, paint)

        // Day of month text
        val dayStr = cal.get(Calendar.DAY_OF_MONTH).toString()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(dayStr, dateX, dateY + 7f, paint)

        // 9. Luxury Hour Hand
        canvas.save()
        canvas.rotate(hourAngle, cx, cy)
        val hourLen = dialRadius * 0.52f
        val hourW = 8f
        // Shadow
        paint.color = Color.parseColor("#66000000")
        canvas.drawRoundRect(RectF(cx - hourW + 4, cy - hourLen + 4, cx + hourW + 4, cy + 18), 4f, 4f, paint)
        // Body (Titanium Sword)
        paint.color = Color.parseColor("#E0E6ED")
        canvas.drawRoundRect(RectF(cx - hourW, cy - hourLen, cx + hourW, cy + 16), 4f, 4f, paint)
        // Luminous Inset
        paint.color = Color.parseColor("#03D5FF")
        canvas.drawRoundRect(RectF(cx - 3f, cy - hourLen + 10f, cx + 3f, cy - 14f), 2f, 2f, paint)
        canvas.restore()

        // 10. Luxury Minute Hand
        canvas.save()
        canvas.rotate(minuteAngle, cx, cy)
        val minLen = dialRadius * 0.78f
        val minW = 6f
        // Shadow
        paint.color = Color.parseColor("#66000000")
        canvas.drawRoundRect(RectF(cx - minW + 4, cy - minLen + 4, cx + minW + 4, cy + 18), 3f, 3f, paint)
        // Body
        paint.color = Color.parseColor("#FFFFFF")
        canvas.drawRoundRect(RectF(cx - minW, cy - minLen, cx + minW, cy + 16), 3f, 3f, paint)
        // Luminous Inset
        paint.color = Color.parseColor("#03D5FF")
        canvas.drawRoundRect(RectF(cx - 2f, cy - minLen + 10f, cx + 2f, cy - 14f), 2f, 2f, paint)
        canvas.restore()

        // 11. Sweeping Cyan Seconds Hand (Chrono Needle with Counterweight)
        canvas.save()
        canvas.rotate(secondAngle, cx, cy)
        val secLen = dialRadius * 0.88f
        paint.color = Color.parseColor("#FF03D5FF")
        paint.strokeWidth = 3f
        canvas.drawLine(cx, cy + dialRadius * 0.22f, cx, cy - secLen, paint)
        // Counterweight Circle
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        canvas.drawCircle(cx, cy + dialRadius * 0.12f, 9f, paint)
        canvas.restore()

        // 12. Center Pinion Cap (Multi-stage jewel screw)
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#152433")
        canvas.drawCircle(cx, cy, 14f, paint)
        paint.color = Color.parseColor("#03D5FF")
        canvas.drawCircle(cx, cy, 8f, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, 3.5f, paint)

        // 13. Glass Refraction Arch Glare (Top specular reflex)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.shader = LinearGradient(
            cx - radius, cy - radius, cx + radius, cy,
            intArrayOf(Color.TRANSPARENT, Color.parseColor("#CCFFFFFF"), Color.TRANSPARENT),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawArc(
            RectF(cx - radius * 0.94f, cy - radius * 0.94f, cx + radius * 0.94f, cy + radius * 0.94f),
            200f, 140f, false, paint
        )

        return bitmap
    }

    // MARK: - Luxury Glass Media Player (720x360 Bitmap)
    fun renderMusicWidget(
        context: Context,
        trackTitle: String,
        artistName: String,
        isPlaying: Boolean,
        width: Int = 720,
        height: Int = 360
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
        val rect = RectF(10f, 10f, width - 10f, height - 10f)

        // 1. Frosted Glass Ambient Backdrop
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#E6180B28"),
                Color.parseColor("#D911071F"),
                Color.parseColor("#F0230F3A")
            ),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, 40f, 40f, paint)

        // 2. Glowing Glass Border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#80D18CFF"),
                Color.parseColor("#3303D5FF"),
                Color.parseColor("#80E040FB")
            ),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, 40f, 40f, paint)

        // 3. 3D Vinyl Record Art (Left Side)
        val discX = 115f
        val discY = height / 2f
        val discR = 85f

        // Disc base
        paint.style = Paint.Style.FILL
        paint.shader = RadialGradient(
            discX, discY, discR,
            intArrayOf(Color.parseColor("#FF2D313A"), Color.parseColor("#FF15171C"), Color.parseColor("#FF0D0E11")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawCircle(discX, discY, discR, paint)

        // Vinyl Grooves
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.2f
        paint.color = Color.parseColor("#22FFFFFF")
        canvas.drawCircle(discX, discY, discR * 0.88f, paint)
        canvas.drawCircle(discX, discY, discR * 0.74f, paint)
        canvas.drawCircle(discX, discY, discR * 0.60f, paint)

        // Center Album Art Sticker
        paint.style = Paint.Style.FILL
        paint.shader = RadialGradient(
            discX, discY, discR * 0.45f,
            intArrayOf(Color.parseColor("#FFD18CFF"), Color.parseColor("#FF7B1FA2"), Color.parseColor("#FF03D5FF")),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawCircle(discX, discY, discR * 0.42f, paint)

        // Center Spindle Hole
        paint.shader = null
        paint.color = Color.parseColor("#FF15171C")
        canvas.drawCircle(discX, discY, 12f, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(discX, discY, 4f, paint)

        // 4. Header Badge
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#CCD18CFF")
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("GLASS HORIZON • NOW PLAYING", 230f, 65f, paint)

        // 5. Track Title & Artist
        paint.color = Color.WHITE
        paint.textSize = 38f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(trackTitle, 230f, 120f, paint)

        paint.color = Color.parseColor("#B3ECEFF1")
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(artistName, 230f, 160f, paint)

        // 6. Glowing Audio Waveform Visualizer Bars (Next to Title)
        val waveStartX = width - 150f
        val waveY = 110f
        val barHeights = floatArrayOf(28f, 54f, 20f, 65f, 40f, 58f, 25f)
        for (b in barHeights.indices) {
            val bx = waveStartX + (b * 16f)
            val bh = if (isPlaying) barHeights[b] else 12f
            paint.shader = LinearGradient(
                bx, waveY - bh / 2f, bx, waveY + bh / 2f,
                Color.parseColor("#FF03D5FF"), Color.parseColor("#FFD18CFF"),
                Shader.TileMode.CLAMP
            )
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(RectF(bx - 3f, waveY - bh / 2f, bx + 3f, waveY + bh / 2f), 3f, 3f, paint)
        }

        // 7. Glowing Track Progress Bar
        val progStartX = 230f
        val progEndX = width - 50f
        val progY = 215f

        // Track background line
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#33FFFFFF")
        canvas.drawRoundRect(RectF(progStartX, progY - 4f, progEndX, progY + 4f), 4f, 4f, paint)

        // Filled progress with Cyan Glow
        val fillEndX = progStartX + (progEndX - progStartX) * 0.58f
        paint.shader = LinearGradient(
            progStartX, progY, fillEndX, progY,
            Color.parseColor("#FFD18CFF"), Color.parseColor("#FF03D5FF"),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(RectF(progStartX, progY - 4f, fillEndX, progY + 4f), 4f, 4f, paint)

        // Scrubber Thumb
        paint.shader = null
        paint.color = Color.WHITE
        canvas.drawCircle(fillEndX, progY, 9f, paint)
        paint.color = Color.parseColor("#03D5FF")
        canvas.drawCircle(fillEndX, progY, 5f, paint)

        // Timestamp text
        paint.color = Color.parseColor("#80FFFFFF")
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("2:24", progStartX, progY + 30f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("4:08", progEndX, progY + 30f, paint)

        return bitmap
    }

    // MARK: - Luxury Glass Weather & Climate (720x360 Bitmap)
    fun renderWeatherWidget(
        context: Context,
        tempStr: String = "28°",
        conditionStr: String = "Sunny & Clear • مشمس وصافٍ",
        isNight: Boolean = false,
        width: Int = 720,
        height: Int = 360
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
        val rect = RectF(10f, 10f, width - 10f, height - 10f)

        // 1. Atmospheric Deep Sky Blue or Midnight Glass
        paint.style = Paint.Style.FILL
        if (isNight) {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    Color.parseColor("#E6091026"),
                    Color.parseColor("#D9040817"),
                    Color.parseColor("#F00F1A3B")
                ),
                null, Shader.TileMode.CLAMP
            )
        } else {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    Color.parseColor("#E60D274A"),
                    Color.parseColor("#D9081A33"),
                    Color.parseColor("#F0143863")
                ),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, 40f, 40f, paint)

        // 2. Glass Border Stroke
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        val borderColors = if (isNight) {
            intArrayOf(
                Color.parseColor("#809C27B0"),
                Color.parseColor("#33FFFFFF"),
                Color.parseColor("#8003D5FF")
            )
        } else {
            intArrayOf(
                Color.parseColor("#804CC3FF"),
                Color.parseColor("#33FFFFFF"),
                Color.parseColor("#8000B0FF")
            )
        }
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            borderColors, null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, 40f, 40f, paint)

        // 3. Radiant Glowing Sun or Luminous Moon Icon (Right Top)
        val celestialX = width - 130f
        val celestialY = 135f
        val celestialR = 48f

        if (isNight) {
            // Moon Glow
            paint.style = Paint.Style.FILL
            paint.shader = RadialGradient(
                celestialX, celestialY, celestialR * 1.7f,
                intArrayOf(Color.parseColor("#8003D5FF"), Color.parseColor("#207C4DFF"), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
            canvas.drawCircle(celestialX, celestialY, celestialR * 1.7f, paint)

            // Moon Disc
            paint.shader = null
            paint.color = Color.parseColor("#FFE0F7FA")
            canvas.drawCircle(celestialX, celestialY, celestialR, paint)
            // Cutout shadow to make crescent
            paint.color = Color.parseColor("#E6091026")
            canvas.drawCircle(celestialX + 16f, celestialY - 12f, celestialR * 0.92f, paint)

            // Twinkling stars
            paint.color = Color.WHITE
            canvas.drawCircle(celestialX - 55f, celestialY - 30f, 2.5f, paint)
            canvas.drawCircle(celestialX - 35f, celestialY + 45f, 2f, paint)
            canvas.drawCircle(celestialX + 45f, celestialY + 35f, 2.5f, paint)
        } else {
            // Corona Glow
            paint.style = Paint.Style.FILL
            paint.shader = RadialGradient(
                celestialX, celestialY, celestialR * 1.8f,
                intArrayOf(Color.parseColor("#80FFD54F"), Color.parseColor("#20FFA000"), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
            canvas.drawCircle(celestialX, celestialY, celestialR * 1.8f, paint)

            // Sun Sphere
            paint.shader = RadialGradient(
                celestialX - 10f, celestialY - 10f, celestialR,
                intArrayOf(Color.parseColor("#FFFFE082"), Color.parseColor("#FFFFB300"), Color.parseColor("#FFF57C00")),
                null, Shader.TileMode.CLAMP
            )
            canvas.drawCircle(celestialX, celestialY, celestialR, paint)
        }

        // 4. Header Bar
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#CC4CC3FF")
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("ATMOSPHERIC • LIVE WEATHER", 40f, 65f, paint)

        // 5. Giant Temperature Readout
        paint.color = Color.WHITE
        paint.textSize = 100f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(tempStr, 38f, 175f, paint)

        // Condition text
        paint.color = Color.WHITE
        paint.textSize = 28f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(conditionStr, 220f, 130f, paint)

        paint.color = Color.parseColor("#B3ECEFF1")
        paint.textSize = 22f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("H: 33°  L: 21° • UV 6 High", 220f, 168f, paint)

        // 6. Pill Metrics (Humidity & Wind)
        val pillY = 270f
        val p1Rect = RectF(40f, pillY - 35f, 330f, pillY + 35f)
        val p2Rect = RectF(350f, pillY - 35f, width - 40f, pillY + 35f)

        paint.color = Color.parseColor("#33000000")
        canvas.drawRoundRect(p1Rect, 22f, 22f, paint)
        canvas.drawRoundRect(p2Rect, 22f, 22f, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = Color.parseColor("#4DFFFFFF")
        canvas.drawRoundRect(p1Rect, 22f, 22f, paint)
        canvas.drawRoundRect(p2Rect, 22f, 22f, paint)

        // Pill text
        paint.style = Paint.Style.FILL
        paint.textSize = 22f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("💧 Humidity: 42%", p1Rect.centerX(), p1Rect.centerY() + 8f, paint)
        canvas.drawText("💨 Wind: 16 km/h N", p2Rect.centerX(), p2Rect.centerY() + 8f, paint)

        return bitmap
    }

    // MARK: - Luxury Glass System & Battery (720x360 Bitmap)
    fun renderSystemWidget(
        context: Context,
        batteryPct: Int,
        isCharging: Boolean,
        ramAvailableGb: String,
        width: Int = 720,
        height: Int = 360
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
        val rect = RectF(10f, 10f, width - 10f, height - 10f)

        // 1. Frosted Cyber Emerald Backdrop
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#E60A261E"),
                Color.parseColor("#D9051713"),
                Color.parseColor("#F00E3328")
            ),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, 40f, 40f, paint)

        // 2. Emerald Glass Border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#804EEDB3"),
                Color.parseColor("#3303D5FF"),
                Color.parseColor("#8000E676")
            ),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, 40f, 40f, paint)

        // 3. Header Bar
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#CC4EEDB3")
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("DEVICE VITALS • GLASS SYSTEM", 40f, 65f, paint)

        // 4. Twin Circular Gauges (Battery & RAM)
        val gaugeR = 85f
        val g1X = width * 0.28f
        val g2X = width * 0.72f
        val gY = 200f

        // --- Gauge 1: Battery ---
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 14f
        paint.color = Color.parseColor("#26FFFFFF")
        canvas.drawArc(RectF(g1X - gaugeR, gY - gaugeR, g1X + gaugeR, gY + gaugeR), 135f, 270f, false, paint)

        // Active Battery Arc
        val batSweep = (batteryPct / 100f) * 270f
        paint.color = if (batteryPct > 20) Color.parseColor("#FF4EEDB3") else Color.parseColor("#FFFF5252")
        canvas.drawArc(RectF(g1X - gaugeR, gY - gaugeR, g1X + gaugeR, gY + gaugeR), 135f, batSweep, false, paint)

        // Battery Readout Text
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 42f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$batteryPct%", g1X, gY + 12f, paint)

        paint.color = Color.parseColor("#B34EEDB3")
        paint.textSize = 18f
        val chargeText = if (isCharging) "⚡ CHARGING" else "BATTERY"
        canvas.drawText(chargeText, g1X, gY + 45f, paint)

        // --- Gauge 2: RAM Memory ---
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 14f
        paint.color = Color.parseColor("#26FFFFFF")
        canvas.drawArc(RectF(g2X - gaugeR, gY - gaugeR, g2X + gaugeR, gY + gaugeR), 135f, 270f, false, paint)

        // Active RAM Arc
        val ramSweep = 190f
        paint.color = Color.parseColor("#FF64B5F6")
        canvas.drawArc(RectF(g2X - gaugeR, gY - gaugeR, g2X + gaugeR, gY + gaugeR), 135f, ramSweep, false, paint)

        // RAM Readout Text
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 34f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(ramAvailableGb, g2X, gY + 12f, paint)

        paint.color = Color.parseColor("#B364B5F6")
        paint.textSize = 18f
        canvas.drawText("RAM FREE", g2X, gY + 45f, paint)

        // Bottom subtext
        paint.color = Color.parseColor("#80FFFFFF")
        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Optimal Performance • All Sensors Healthy", width / 2f, height - 30f, paint)

        return bitmap
    }
}
