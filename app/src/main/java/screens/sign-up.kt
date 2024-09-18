package screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.identity.trace.R


class SignUp : ComponentActivity() {

    // UI Components
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var signUpBtn: Button
    private lateinit var navigateToSignInBtn: Button
    private lateinit var back_to_sign_in_page: Button
    private lateinit var signed_up_gif: ImageView

    // Firebase Firestore instance
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        // Initialize UI Components
        initUI()

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Set onClickListeners for buttons
        setOnClickListenersForAllButtons()
        initGifs()
    }

    // Initialize UI components
    private fun initUI() {
        editTextFirstName = findViewById(R.id.sign_up_first_name)
        editTextLastName = findViewById(R.id.sign_up_last_name)
        editTextEmail = findViewById(R.id.sign_up_email)
        editTextPassword = findViewById(R.id.sign_up_password)
        editTextConfirmPassword = findViewById(R.id.sign_up_confirm_password)
        signUpBtn = findViewById(R.id.sign_up_btn)
        back_to_sign_in_page = findViewById(R.id.back_to_sign_in_page)
        signed_up_gif = findViewById(R.id.signed_up_gif)
        navigateToSignInBtn = findViewById(R.id.navigate_to_sign_in_btn)
    }
    private fun initGifs() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.signup_gif)
            .override(150, 150)
            .into(signed_up_gif)
    }

    // Set onClickListeners for buttons
    private fun setOnClickListenersForAllButtons() {
        Toast.makeText(this, "setOnClickListenersForAllButtons", Toast.LENGTH_SHORT).show()
        signUpBtn.setOnClickListener {
            val errorMessage = validateFields()
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                // After validation is successful, save data to Firebase
                saveSignedUpUserToFirebase()
            }
        }

        navigateToSignInBtn.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }, 2000)
        }
        back_to_sign_in_page.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                finish() // This will go back to the previous activity
            }, 2000)
        }
    }

    // Validate input fields and return an error message if any validation fails
    private fun validateFields(): String? {
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        return when {
            firstName.isEmpty() -> "Please enter your first name."
            lastName.isEmpty() -> "Please enter your last name."
            email.isEmpty() -> "Please enter your email address."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address."
            password.isEmpty() -> "Please enter your password."
            confirmPassword.isEmpty() -> "Please confirm your password."
            password != confirmPassword -> "Passwords do not match."
            else -> null
        }
    }

    private fun saveSignedUpUserToFirebase() {


        Toast.makeText(this, "Attempting to save user to Firebase", Toast.LENGTH_SHORT).show()

        // Retrieve and trim input fields
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user object
        val user = User(firstName, lastName, email, password)

        // Get references
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("signed-up-users")
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        // Sanitize email for database key
        val emailKey = email.replace(".", ",")

        // Check if the email already exists in Realtime Database
        userRef.child(emailKey).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Email already exists in Realtime Database
                    Toast.makeText(this, "User with this email already exists in our system!", Toast.LENGTH_SHORT).show()
                } else {
                    // Check if email is already registered with Google Sign-In
                    auth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val signInMethods = task.result?.signInMethods
                                if (signInMethods != null && signInMethods.isNotEmpty()) {
                                    // Email already exists in Google Sign-In
                                    Toast.makeText(this, "User with this email already exists with Google Sign-In!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Save user to Realtime Database if the email is not already in use
                                    userRef.child(emailKey).setValue(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()

                                            // Navigate to Sign In screen after 2 seconds
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                val intent = Intent(this, SignIn::class.java)
                                                startActivity(intent)
                                            }, 2000)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("DatabaseError", "Failed to save user to Realtime Database", e)
                                            Toast.makeText(this, "Failed to register: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Log.e("AuthError", "Error checking email with Firebase Authentication", task.exception)
                                Toast.makeText(this, "Error checking email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("DatabaseError", "Error checking email in Realtime Database", e)
                Toast.makeText(this, "Error checking email: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }



}

// User data model class
data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "" // In a real app, avoid storing plain text passwords
)
