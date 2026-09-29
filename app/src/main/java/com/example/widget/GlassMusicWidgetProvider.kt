package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class GlassMusicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                isPlaying = !isPlaying
                updateAllWidgets(context)
            }
            ACTION_NEXT -> {
                currentTrackIndex = (currentTrackIndex + 1) % playlist.size
                isPlaying = true
                updateAllWidgets(context)
            }
            ACTION_PREV -> {
                currentTrackIndex = if (currentTrackIndex - 1 < 0) playlist.size - 1 else currentTrackIndex - 1
                isPlaying = true
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.widget.ACTION_MUSIC_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.widget.ACTION_MUSIC_NEXT"
        const val ACTION_PREV = "com.example.widget.ACTION_MUSIC_PREV"

        data class Track(val title: String, val artist: String, val duration: String)

        private val playlist = listOf(
            Track("Midnight City", "M83 • Synthwave Glass", "2:14 / 4:03"),
            Track("Starboy", "The Weeknd • Neon Horizon", "1:45 / 3:50"),
            Track("Nightcall", "Kavinsky • Cyberpunk Drive", "3:10 / 4:18"),
            Track("Resonance", "HOME • Chillwave Ambient", "0:58 / 3:32"),
            Track("After Hours", "The Weeknd • Atmospheric", "2:40 / 6:01")
        )

        private var currentTrackIndex = 0
        private var isPlaying = true

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, GlassMusicWidgetProvider::class.java)
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
            val views = RemoteViews(context.packageName, R.layout.glass_music_widget_layout)
            val track = playlist[currentTrackIndex]

            // Render Luxury Canvas Media Player Bitmap
            val bitmap = GlassWidgetRenderer.renderMusicWidget(
                context = context,
                trackTitle = track.title,
                artistName = track.artist,
                isPlaying = isPlaying,
                width = 720,
                height = 360
            )
            views.setImageViewBitmap(R.id.widget_music_canvas, bitmap)

            // Play / Pause Icon toggle
            val playIcon = if (isPlaying) R.drawable.ic_music_pause else R.drawable.ic_music_play
            views.setImageViewResource(R.id.btn_music_play, playIcon)

            // Interactive Play/Pause PendingIntent
            val playIntent = Intent(context, GlassMusicWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val playPendingIntent = PendingIntent.getBroadcast(
                context, 101, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_music_play, playPendingIntent)

            // Interactive Next PendingIntent
            val nextIntent = Intent(context, GlassMusicWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context, 102, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_music_next, nextPendingIntent)

            // Interactive Prev PendingIntent
            val prevIntent = Intent(context, GlassMusicWidgetProvider::class.java).apply {
                action = ACTION_PREV
            }
            val prevPendingIntent = PendingIntent.getBroadcast(
                context, 103, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_music_prev, prevPendingIntent)

            // Tap on background to open app
            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(
                context, 104, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_music_canvas, appPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
