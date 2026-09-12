package com.example.camerareticle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class ReticleOverlayView(context: Context, private var style: Int, private var alphaPct: Int) : View(context) {

    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 3f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    init {
        setAlphaPercent(alphaPct)
    }

    fun setStyle(newStyle: Int) {
        style = newStyle
        invalidate()
    }

    fun setAlphaPercent(pct: Int) {
        alphaPct = pct
        paint.alpha = (255 * (pct.coerceIn(5, 100) / 100f)).toInt()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2
        val cy = h / 2

        when (style) {
            0 -> { // Crosshair
                canvas.drawLine(cx, 0f, cx, h, paint)
                canvas.drawLine(0f, cy, w, cy, paint)
            }
            1 -> { // Rule of thirds
                canvas.drawLine(w / 3, 0f, w / 3, h, paint)
                canvas.drawLine(2 * w / 3, 0f, 2 * w / 3, h, paint)
                canvas.drawLine(0f, h / 3, w, h / 3, paint)
                canvas.drawLine(0f, 2 * h / 3, w, 2 * h / 3, paint)
            }
            2 -> { // Center dot
                canvas.drawCircle(cx, cy, 8f, paint.apply { style = Paint.Style.FILL })
                paint.style = Paint.Style.STROKE
            }
            3 -> { // Circle guide
                val radius = minOf(w, h) / 4
                canvas.drawCircle(cx, cy, radius, paint)
                canvas.drawLine(cx, cy - 15f, cx, cy + 15f, paint)
                canvas.drawLine(cx - 15f, cy, cx + 15f, cy, paint)
            }
        }
    }
}
