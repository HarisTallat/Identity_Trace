package screens

import android.os.Bundle
import android.widget.Button
import android.widget.EditText

import android.widget.Toast
import androidx.activity.ComponentActivity

import com.identity.trace.R
import java.util.Properties

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSupport : ComponentActivity() {

    private lateinit var editTextSupportName: EditText
    private lateinit var editTextSupportEmail: EditText
    private lateinit var editTextSupportDescription: EditText
    private lateinit var buttonSupportSubmit: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_support)



        buttonSupportSubmit.setOnClickListener {
            val message = editTextSupportDescription.text.toString()
            sendEmail(message)
        }
    }

    private fun initUI() {
        editTextSupportName = findViewById(R.id.editTextSupportName);
        editTextSupportEmail = findViewById(R.id.editTextSupportEmail);
        editTextSupportDescription = findViewById(R.id.editTextSupportDescription);
        buttonSupportSubmit = findViewById(R.id.buttonSupportSubmit);

    }

    private fun sendEmail(message:String) {


    }


}