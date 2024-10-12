package screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.identity.trace.R
import screens.missingpersonforms.AddMissingPersonActivity
import screens.missingpersonforms.SearchMissingPersonActivity

class Home : ComponentActivity() {

    private lateinit var loginBtn: Button
    private lateinit var searchPersonBtn: Button
    private lateinit var addDetailsBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.home)

        // Initialize UI components
        initUI()

        // Set OnClickListeners
        setOnClickListenersForAllButtons()
    }

    private fun initUI() {
        loginBtn = findViewById(R.id.loginBtn) // Initialize login button
        addDetailsBtn = findViewById(R.id.add_details) // Initialize login button
        searchPersonBtn = findViewById(R.id.search_person) // Initialize login button
    }

    private fun setOnClickListenersForAllButtons() {
        loginBtn.setOnClickListener {
            navigateToSignInScreen()
        }
        addDetailsBtn.setOnClickListener {
            navigateToAddMissingPersonScreen()
        }
        searchPersonBtn.setOnClickListener {
            navigateToSearchMissingPersonScreen()
        }
    }

    private fun navigateToAddMissingPersonScreen() {

        val intent = Intent(this, AddMissingPersonActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSearchMissingPersonScreen() {
        val intent = Intent(this, SearchMissingPersonActivity::class.java)
        startActivity(intent)
    } private fun navigateToSignInScreen() {
        // Navigate to SignInActivity
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
    }
}
