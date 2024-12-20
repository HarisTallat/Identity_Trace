package screens.main

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.identity.trace.R
import screens.dashboard.DashboardActivity

class MainActivity : ComponentActivity() {
    private lateinit var splashScreenGif: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initGifs()
        // Start a 3-second countdown
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                // Navigate to DashboardActivity
                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                finish()
            }
        }.start()
    }

    private fun initGifs() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.search) // Ensure this is a valid GIF resource
            .override(150, 150)
            .into(splashScreenGif)
    }

    private fun initUI() {
        splashScreenGif = findViewById(R.id.splash_screen_gif)
    }
}
