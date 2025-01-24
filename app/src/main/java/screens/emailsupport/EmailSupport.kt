package screens.emailsupport

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.identity.trace.R
import config.Config
import screens.SignIn
import screens.dashboard.DashboardActivity
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSupportActivity : ComponentActivity() {
    private lateinit var editTextSupportName: EditText
    private lateinit var editTextSupportEmail: EditText
    private lateinit var editTextSupportDescription: EditText
    private lateinit var editTextSupportSubject: EditText
    private lateinit var buttonSupportSubmit: Button
    private lateinit var emailSupportBackButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_support)
        initUI()

        bottomNavigationView.itemIconTintList = getColorStateList(R.color.white)
        bottomNavigationView.itemTextColor = getColorStateList(R.color.white)

        emailSupportBackButton.setOnClickListener {
            navigateToHomeScreen()
        }

        bottomNavigationView.itemIconTintList = getColorStateList(R.color.white)
        bottomNavigationView.itemTextColor = getColorStateList(R.color.white)

        // Handle intent that comes from EmailSupportActivity
        val intent = intent
        if (intent.hasExtra("SELECT_TAB")) {
            val selectedTab = intent.getStringExtra("SELECT_TAB")
            if (selectedTab == "HOME") {
                bottomNavigationView.selectedItemId = R.id.nav_home
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_support -> {
                    // Navigate to Support Screen
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, SignIn::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        buttonSupportSubmit.setOnClickListener {
            val message = editTextSupportDescription.text.toString()
            if (message.isNotEmpty()) {
                sendEmail(message)
            } else {
                Toast.makeText(this, "Please fill in the support description.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun navigateToHomeScreen() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun initUI() {
        editTextSupportName = findViewById(R.id.editTextSupportName)
        editTextSupportEmail = findViewById(R.id.editTextSupportEmail)
        editTextSupportDescription = findViewById(R.id.editTextSupportDescription)
        buttonSupportSubmit = findViewById(R.id.buttonSupportSubmit)
        editTextSupportSubject = findViewById(R.id.editTextSupportSubject)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        emailSupportBackButton = findViewById(R.id.back_to_main_page_from_email_support)
    }

    private fun sendEmail(message: String) {
        try {
            val properties = Config.MAIL_PROPERTIES
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Config.SENDER_EMAIL, Config.PASSWORD)
                }
            })

            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(Config.RECEIVER_EMAIL))
            mimeMessage.subject = editTextSupportSubject.text.toString()
            mimeMessage.setText(message)

            val t = Thread {
                try {
                    Transport.send(mimeMessage)
                    runOnUiThread {
                        Toast.makeText(this, "Email Sent Successfully", Toast.LENGTH_SHORT).show()

                        // Navigate back to DashboardActivity (Home) and select the Home tab
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("SELECT_TAB", "HOME")
                        startActivity(intent)
                        finish() // Close this activity
                    }
                } catch (e: MessagingException) {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to Send your Message To Support", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                }
            }
            t.start()
        } catch (e: AddressException) {
            Toast.makeText(this, "Failed to Send your Message To Support", Toast.LENGTH_SHORT).show()
        } catch (e: MessagingException) {
            Toast.makeText(this, "Failed to Send your Message To Support", Toast.LENGTH_SHORT).show()
        }
    }
}
