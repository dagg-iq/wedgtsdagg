package com.example.widget

import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import java.util.Locale

class GlassSystemWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.glass_system_widget_layout)

            // Read Real Battery Status
            val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, batteryFilter)
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 85
            views.setTextViewText(R.id.system_battery_level, "$batteryPct%")

            if (isCharging) {
                views.setTextViewText(R.id.system_battery_state, "⚡ Charging")
                views.setTextViewText(R.id.system_charging_badge, "CHARGING")
            } else {
                views.setTextViewText(R.id.system_battery_state, "🔋 On Battery")
                views.setTextViewText(R.id.system_charging_badge, "HEALTHY")
            }

            // Read Real RAM Available
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            if (actManager != null) {
                actManager.getMemoryInfo(memInfo)
                val availGb = memInfo.availMem.toDouble() / (1024 * 1024 * 1024)
                views.setTextViewText(R.id.system_ram_level, String.format(Locale.US, "%.1f GB", availGb))
            }

            // Launch MainActivity when tapped
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                300,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_system_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
