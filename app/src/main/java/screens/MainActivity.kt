
package screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.identity.trace.R

class MainActivity : ComponentActivity() {
    private lateinit var splash_screen_gif: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()

        initGifs()
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToHomeScreen()
        }, 5000)

    }
    private fun initUI() {
        splash_screen_gif = findViewById(R.id.splash_screen_gif) // Initialize login button
    }
    private fun  initGifs(){
        Glide.with(this)
            .asGif()
            .load(R.drawable.search)
            .into(splash_screen_gif)
    }
    private fun navigateToHomeScreen() {
        val intent = Intent(this, Home::class.java) // Ensure Home activity exists
        startActivity(intent)
        finish() // Optional: Call finish if you don't want to keep MainActivity in the back stack
    }
}
