package com.easylife.floatingwindowdemo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        // ⚡ STEP 1: Immediately startForeground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "overlay_channel"
            val channel = NotificationChannel(
                channelId,
                "Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Overlay Running")
                .setContentText("Floating banner is active")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

            // ⚡ MUST be called immediately to avoid crash
            startForeground(1, notification)
        }

        // ⚡ STEP 2: Now show the overlay
        showOverlay()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Keep service running
        return START_STICKY
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        val textView = TextView(this).apply {
            text = "🔔 এই মোবাইল টি কিস্তিতে কেনা !"
            textSize = 18f
            setBackgroundColor(Color.BLACK)
            setTextColor(Color.WHITE)
            setPadding(20, 20, 20, 20)
        }

        overlayView = textView
        windowManager.addView(overlayView, params)
    }

    /*override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager.removeView(it) }
    }*/

}

