package screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.identity.trace.R

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_missing_person_form) // Replace with your layout

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

//        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
//
//        val signInButton: Button = findViewById(R.id.sigin_in) // Your Button ID
//        signInButton.setOnClickListener {
//            signInWithGoogle()
//        }


    }

    private fun signInWithGoogle() {

        val signInIntent = googleSignInClient.signInIntent
        Log.e("MainActivity", "Google sign in failed")
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
                Log.e("MainActivity", "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    val user = firebaseAuth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.email}", Toast.LENGTH_SHORT).show()
                    // Store the user's email in Firestore
                    user?.email?.let { storeUserEmail(it) }
                } else {
                    // Sign-in failed
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun storeUserEmail(email: String) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf("email" to email)

        Log.d("MainActivity", "Attempting to store email: $email") // Log the email being stored

        db.collection("users").add(user)
            .addOnSuccessListener {
                Log.d("MainActivity", "Email stored successfully: $email") // Log success
                Toast.makeText(this, "Email stored successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error storing email: ${e.message}", e) // Log failure
                Toast.makeText(this, "Error storing email", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
