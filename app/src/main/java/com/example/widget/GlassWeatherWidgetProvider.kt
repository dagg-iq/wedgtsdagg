package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class GlassWeatherWidgetProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.glass_weather_widget_layout)

            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val isNight = hour < 6 || hour >= 19

            val tempStr = if (isNight) "24°" else "29°"
            val conditionStr = if (isNight) "Clear Night • سماء صافية" else "Sunny • مشمس وصافٍ"
            val badgeStr = if (isNight) "NIGHT" else "SUNNY"
            val iconRes = if (isNight) R.drawable.ic_weather_cloud else R.drawable.ic_weather_sun

            views.setTextViewText(R.id.weather_temp, tempStr)
            views.setTextViewText(R.id.weather_condition, conditionStr)
            views.setTextViewText(R.id.weather_status_badge, badgeStr)
            views.setImageViewResource(R.id.weather_icon, iconRes)

            // Launch MainActivity when tapped
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                200,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_weather_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
