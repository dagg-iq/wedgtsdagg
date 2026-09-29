package com.example.widget

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

data class InstalledAppItem(
    val appName: String,
    val packageName: String,
    val iconDrawable: Drawable?,
    val isSystemPreset: Boolean = false
)

object GlassShortcutHelper {

    /**
     * Retrieves all installed user-launchable apps on the device (sorted alphabetically)
     */
    fun getInstalledApps(context: Context): List<InstalledAppItem> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val list = mutableListOf<InstalledAppItem>()

        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            // Skip our own app from the target list
            if (pkg == context.packageName) continue

            val name = try {
                info.loadLabel(pm).toString()
            } catch (_: Exception) {
                pkg
            }

            val icon = try {
                info.loadIcon(pm)
            } catch (_: Exception) {
                null
            }

            list.add(InstalledAppItem(appName = name, packageName = pkg, iconDrawable = icon))
        }

        return list.sortedBy { it.appName.lowercase() }
    }

    /**
     * Pins a custom glass icon shortcut directly to the Android Home Screen (Desktop)
     */
    fun pinAppShortcut(
        context: Context,
        packageName: String,
        appName: String,
        glassIconBitmap: Bitmap
    ): Boolean {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            Toast.makeText(context, "إضافة الاختصارات غير مدعومة في لانشر جهازك", Toast.LENGTH_LONG).show()
            return false
        }

        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            Toast.makeText(context, "تعذر العثور على تشغيل للتطبيق: $appName", Toast.LENGTH_SHORT).show()
            return false
        }

        launchIntent.action = Intent.ACTION_MAIN
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val shortcutId = "glass_shortcut_${packageName}_${System.currentTimeMillis()}"

        val shortcutInfo = ShortcutInfoCompat.Builder(context, shortcutId)
            .setShortLabel(appName)
            .setLongLabel(appName)
            .setIcon(IconCompat.createWithBitmap(glassIconBitmap))
            .setIntent(launchIntent)
            .build()

        val success = ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
        if (success) {
            Toast.makeText(context, "تم إرسال الأيقونة الزجاجية إلى الشاشة الرئيسية! اضغط إضافة", Toast.LENGTH_LONG).show()
        }
        return success
    }

    /**
     * Exports a high-res Glass Icon to the device's Pictures/GlassIcons folder
     */
    fun exportIconToGallery(context: Context, appName: String, bitmap: Bitmap): Boolean {
        return try {
            val filename = "Glass_${appName.replace(" ", "_")}_${System.currentTimeMillis()}.png"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/GlassIcons")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, values, null, null)
                    Toast.makeText(context, "تم حفظ الأيقونة في المعرض (Pictures/GlassIcons)", Toast.LENGTH_SHORT).show()
                    true
                } else false
            } else {
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GlassIcons")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                Toast.makeText(context, "تم حفظ الأيقونة في المعرض", Toast.LENGTH_SHORT).show()
                true
            }
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر الحفظ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
