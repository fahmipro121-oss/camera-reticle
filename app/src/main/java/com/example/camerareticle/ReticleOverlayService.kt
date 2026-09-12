package com.example.camerareticle

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.app.NotificationCompat

class ReticleOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayRoot: FrameLayout? = null
    private lateinit var reticleView: ReticleOverlayView
    private lateinit var params: WindowManager.LayoutParams

    private val NOTIF_CHANNEL = "reticle_channel"
    private val NOTIF_ID = 1001

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification())

        val style = intent?.getIntExtra("style", 0) ?: 0
        val opacity = intent?.getIntExtra("opacity", 80) ?: 80

        if (overlayRoot == null) {
            showOverlay(style, opacity)
        } else {
            reticleView.setStyle(style)
            reticleView.setAlphaPercent(opacity)
        }

        return START_STICKY
    }

    private fun showOverlay(style: Int, opacity: Int) {
        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0

        val root = FrameLayout(this)
        reticleView = ReticleOverlayView(this, style, opacity)
        root.addView(
            reticleView,
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )

        // Small draggable close handle so the user can remove the overlay without
        // reopening the app. Positioned top-right, ~48dp button.
        val closeBtn = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            alpha = 0.6f
            setBackgroundColor(0x00000000)
        }
        val closeParams = FrameLayout.LayoutParams(90, 90).apply {
            gravity = Gravity.TOP or Gravity.END
            topMargin = 40
            rightMargin = 40
        }
        closeBtn.setOnClickListener { stopSelf() }
        root.addView(closeBtn, closeParams)

        // Drag-to-move: touch anywhere on the reticle area to reposition the whole
        // overlay (useful if you want it offset instead of full-screen centered).
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        reticleView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(root, params)
                    true
                }
                else -> false
            }
        }

        windowManager.addView(root, params)
        overlayRoot = root
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIF_CHANNEL, "Reticle Overlay", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, NOTIF_CHANNEL)
            .setContentTitle("Camera Reticle aktif")
            .setContentText("Ketuk untuk membuka pengaturan")
            .setSmallIcon(android.R.drawable.ic_menu_crop)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayRoot?.let { windowManager.removeView(it) }
        overlayRoot = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
