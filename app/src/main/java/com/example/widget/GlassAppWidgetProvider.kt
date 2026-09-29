package com.example.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class GlassAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        scheduleMinuteUpdates(context)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleMinuteUpdates(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelMinuteUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_CLOCK_TICK ||
            intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_CLOCK_TICK = "com.example.widget.ACTION_CLOCK_TICK"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, GlassAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.glass_appwidget_layout)

            // Render Luxury Horology Analog Clock Canvas Bitmap
            val bitmap = GlassWidgetRenderer.renderLuxuryClock(context, 600, 600)
            views.setImageViewBitmap(R.id.widget_clock_canvas, bitmap)

            // Launch MainActivity when tapped
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun scheduleMinuteUpdates(context: Context) {
            val intent = Intent(context, GlassAppWidgetProvider::class.java).apply {
                action = ACTION_CLOCK_TICK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 888, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 60000L,
                60000L,
                pendingIntent
            )
        }

        private fun cancelMinuteUpdates(context: Context) {
            val intent = Intent(context, GlassAppWidgetProvider::class.java).apply {
                action = ACTION_CLOCK_TICK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 888, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
        }
    }
}
