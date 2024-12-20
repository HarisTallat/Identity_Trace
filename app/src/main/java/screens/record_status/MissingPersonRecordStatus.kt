package screens.record_status

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.identity.trace.R
import screens.dashboard.DashboardActivity

class MissingPersonRecordStatus : AppCompatActivity() {

    private lateinit var showStatusMessage: TextView
    private lateinit var statusImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searched_record_status)

        // Initialize UI components
        showStatusMessage = findViewById(R.id.record_status) // Ensure this ID exists in your layout
        statusImage = findViewById(R.id.record_image_status) // Ensure this ID exists in your layout

        // Retrieve the 'check' flag from the intent
        val isCheckTrue = intent.getBooleanExtra("check", false)
        // Update the UI based on the flag
        if (isCheckTrue) {
            showStatusMessage.text = "The image provided matches a record in our database. Thank you for your submission."
            statusImage.setImageResource(R.drawable.found) // Corrected method to set the image
        } else {
            showStatusMessage.text = "The image provided does not match any record in our database. Please try again with different details."
            statusImage.setImageResource(R.drawable.not_found) // Corrected method to set the image
        }
        // Set a delay of 3 seconds before navigating back to the Dashboard
        Handler().postDelayed({
            // Navigate back to the Dashboard activity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()  // Optional: Finish the current activity so it's not kept in the back stack
        }, 3000)  // 3000 milliseconds = 3 seconds
    }
}
