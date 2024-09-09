package config

import java.util.Properties

object Config {
    const val SENDER_EMAIL = "identitytrace97@gmail.com"
    const val PASSWORD = "vwot wrsq seuh kpfx"
    const val RECEIVER_EMAIL = "talha.ansari.zahid@gmail.com"
    const val STRING_HOST = "smtp.gmail.com"


    val MAIL_PROPERTIES: Properties = System.getProperties().apply {
        put("mail.smtp.host", STRING_HOST)
        put("mail.smtp.port", "587")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.auth", "true")
    }
}