package com.example.camerareticle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val styles = listOf("Crosshair", "Rule of Thirds", "Center Dot", "Circle Guide")
    private var overlayActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner = findViewById<Spinner>(R.id.spinnerStyle)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, styles)

        val seekOpacity = findViewById<SeekBar>(R.id.seekOpacity)
        val btnToggle = findViewById<Button>(R.id.btnToggle)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)

        btnToggle.setOnClickListener {
            if (!overlayActive) {
                if (!Settings.canDrawOverlays(this)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                    Toast.makeText(this, "Izinkan 'Display over other apps' lalu tekan tombol lagi", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val styleIndex = spinner.selectedItemPosition
                val opacity = seekOpacity.progress

                val serviceIntent = Intent(this, ReticleOverlayService::class.java)
                serviceIntent.putExtra("style", styleIndex)
                serviceIntent.putExtra("opacity", opacity)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }

                overlayActive = true
                btnToggle.text = "Sembunyikan Overlay"
                txtStatus.text = "Status: aktif"
            } else {
                stopService(Intent(this, ReticleOverlayService::class.java))
                overlayActive = false
                btnToggle.text = "Tampilkan Overlay"
                txtStatus.text = "Status: nonaktif"
            }
        }
    }
}
