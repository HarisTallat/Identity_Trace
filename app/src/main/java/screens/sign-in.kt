package screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.identity.trace.R

class SignIn : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signInBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var googleSignInBtn: ImageButton
    private lateinit var enteredEmail: EditText
    private lateinit var enteredPassword: EditText
    private lateinit var back_to_main_page_from_sign_in: Button
    private lateinit var search_gif: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        // Initialize Google Sign-In client and FirebaseAuth
        initGoogleSignInClient()

        // Buttons and input initialization
        initUI()
        initGifs()

        // Set onClickListeners
        setOnClickListenersForAllButtons()
    }
private fun  initGifs(){
        Glide.with(this).asGif().load(R.drawable.search).into(search_gif)
}
    private fun initUI() {
        googleSignInBtn = findViewById(R.id.google_sign_in)
        signUpBtn = findViewById(R.id.navigate_to_sign_up_btn)
        signInBtn = findViewById(R.id.ui_sign_in_btn)
        enteredEmail = findViewById(R.id.sign_in_email)
        enteredPassword = findViewById(R.id.sign_in_password)
        search_gif = findViewById(R.id.search_gif)
        back_to_main_page_from_sign_in = findViewById(R.id.back_to_main_page_from_sign_in)
    }

    private fun initGoogleSignInClient() {
        firebaseAuth = FirebaseAuth.getInstance()
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun setOnClickListenersForAllButtons() {
        signUpBtn.setOnClickListener {
            navigateToSignUpScreen()
        }
        back_to_main_page_from_sign_in.setOnClickListener {
                navigateToHomeScreen()
        }

        googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        signInBtn.setOnClickListener {
            val email = enteredEmail.text.toString().trim()
            val password = enteredPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                validateUserCredentials(email, password)
            } else {
                showErrorMessage("Please enter both email and password!")
            }
        }
    }

    private fun validateUserCredentials(email: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val usersReference = database.getReference("signed-up-users")

        usersReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                        if (storedPassword != null && storedPassword == password) {
                            Toast.makeText(this@SignIn, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                            navigateToHomeScreen()
                            return
                        }
                    }
                    showErrorMessage("Provide the correct credentials")
                } else {
                    showErrorMessage("Provide the correct credentials!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SignIn", "Database error: ${error.message}")
            }
        })
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSignUpScreen() {
        Toast.makeText(this, "Navigating to Sign Up", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }
    private fun navigateToHomeScreen() {
        Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    private fun navigateToMainScreen() {
        Toast.makeText(this, "Navigating to Main Screen", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("SignIn", "Google sign-in failed", e)
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.email}", Toast.LENGTH_SHORT).show()
                    user?.email?.let {

                        navigateToHomeScreen()
                    }
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

//    private fun storeUserEmail(email: String) {
    //storeUserEmail(it)
//        val db = FirebaseFirestore.getInstance()
//        val user = hashMapOf("email" to email)
//
//        Log.d("okokkkkkk", "Attempting to store email: $email")
//
//        db.collection("signed-up-users").add(user)
//            .addOnSuccessListener {
//                Log.d("okokkkkkk", "Email stored successfully: $email")
//                Toast.makeText(this, "Email stored successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Log.e("okokkkkkk", "Error storing email: ${e.message}", e)
//                Toast.makeText(this, "Error storing email: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
