package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.RemoteViews
import com.example.R

class GlassDockWidgetProvider : AppWidgetProvider() {

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
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, GlassDockWidgetProvider::class.java)
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
            val views = RemoteViews(context.packageName, R.layout.glass_dock_widget_layout)

            // Render 4 iOS Pure Glass Icons
            val iconPhone = GlassIconRenderer.renderIosPureGlassIcon("phone", "Phone", GlassIconStyle.IOS_LIQUID_GLASS, null, 180)
            val iconMessages = GlassIconRenderer.renderIosPureGlassIcon("messages", "Messages", GlassIconStyle.IOS_LIQUID_GLASS, null, 180)
            val iconBrowser = GlassIconRenderer.renderIosPureGlassIcon("browser", "Chrome", GlassIconStyle.IOS_LIQUID_GLASS, null, 180)
            val iconCamera = GlassIconRenderer.renderIosPureGlassIcon("camera", "Camera", GlassIconStyle.IOS_LIQUID_GLASS, null, 180)

            val dockBitmap = GlassIconRenderer.renderGlassDockWidget(
                context = context,
                appIcons = listOf(iconPhone, iconMessages, iconBrowser, iconCamera),
                appLabels = listOf("Phone", "Chat", "Browser", "Camera"),
                width = 720,
                height = 200
            )

            views.setImageViewBitmap(R.id.widget_dock_canvas, dockBitmap)

            // Slot 1: Phone / Dialer
            val dialIntent = Intent(Intent.ACTION_DIAL)
            val dialPending = PendingIntent.getActivity(
                context, 501, dialIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.dock_slot_1, dialPending)

            // Slot 2: Messages / Chat
            val msgIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            val resolvedMsg = context.packageManager.resolveActivity(msgIntent, 0)
            val targetMsgIntent = if (resolvedMsg != null) msgIntent else {
                context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                    ?: Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            val msgPending = PendingIntent.getActivity(
                context, 502, targetMsgIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.dock_slot_2, msgPending)

            // Slot 3: Browser (Chrome)
            val browserIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_BROWSER)
            }
            val resolvedBrowser = context.packageManager.resolveActivity(browserIntent, 0)
            val targetBrowserIntent = if (resolvedBrowser != null) browserIntent else {
                context.packageManager.getLaunchIntentForPackage("com.android.chrome") ?: browserIntent
            }
            val browserPending = PendingIntent.getActivity(
                context, 503, targetBrowserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.dock_slot_3, browserPending)

            // Slot 4: Camera
            val camIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            val camPending = PendingIntent.getActivity(
                context, 504, camIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.dock_slot_4, camPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
