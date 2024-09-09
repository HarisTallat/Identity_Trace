package screens.emailsupport

import android.os.Bundle
import android.widget.Button
import android.widget.EditText

import android.widget.Toast
import androidx.activity.ComponentActivity

import com.identity.trace.R
import config.Config

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
    private lateinit var editTextSupportProject: EditText
    private lateinit var buttonSupportSubmit: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_support)
        initUI()

        buttonSupportSubmit.setOnClickListener {
            val message = editTextSupportDescription.text.toString()
            sendEmail(message)
        }
    }

    private fun initUI() {
        editTextSupportName = findViewById(R.id.editTextSupportName)
        editTextSupportEmail = findViewById(R.id.editTextSupportEmail)
        editTextSupportDescription = findViewById(R.id.editTextSupportDescription)
        buttonSupportSubmit = findViewById(R.id.buttonSupportSubmit)
        editTextSupportProject = findViewById(R.id.editTextSupportSubject)
    }

    private fun sendEmail(message:String) {
        try {
            val properties = Config.MAIL_PROPERTIES
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Config.SENDER_EMAIL, Config.PASSWORD)
                }
            })
            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress((Config.RECEIVER_EMAIL)))
            mimeMessage.subject = editTextSupportProject.text.toString();
            mimeMessage.setText(message)

            val t = Thread {
                try {
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {

                    Toast.makeText(this, "Failed to Send your Message To Support", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            t.start()
        } catch (e: AddressException) {
            Toast.makeText(this, "Failed to Send your Message To Support", Toast.LENGTH_SHORT).show()
        } catch (e: MessagingException) {
            Toast.makeText(this, "Failed to Send your Message To Support", Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(this, "Email Sent Successfully", Toast.LENGTH_SHORT).show()
    }
}