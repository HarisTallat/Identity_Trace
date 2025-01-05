package screens.missingpersonforms

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.identity.trace.R

class ConsentFormActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consent_form)

        val radioGroupPermission: RadioGroup = findViewById(R.id.radioGroupPermission)
        val radioGroupDataUsage: RadioGroup = findViewById(R.id.radioGroupDataUsage)
        val radioGroupAccuracy: RadioGroup = findViewById(R.id.radioGroupAccuracy)
        val buttonSubmitConsent: Button = findViewById(R.id.buttonSubmitConsent)

        buttonSubmitConsent.setOnClickListener {
            val isPermissionGranted = getSelectedOption(radioGroupPermission)
            val isDataUsageAgreed = getSelectedOption(radioGroupDataUsage)
            val isAccuracyConfirmed = getSelectedOption(radioGroupAccuracy)

            if (isPermissionGranted == null || isDataUsageAgreed == null || isAccuracyConfirmed == null) {
                Toast.makeText(this, "Please answer all questions before submitting.", Toast.LENGTH_SHORT).show()
            } else {
                val consentGranted = isPermissionGranted && isDataUsageAgreed && isAccuracyConfirmed
                if (consentGranted) {
                    // Handle successful consent
                    Toast.makeText(this, "Thank you! Consent submitted successfully.", Toast.LENGTH_SHORT).show()

                    // Proceed to the Add Person form
                    val intent = Intent(this, AddMissingPersonActivity::class.java)  // Assuming AddPersonFormActivity is your next screen
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Consent not fully granted. Please review your answers.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getSelectedOption(radioGroup: RadioGroup): Boolean? {
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            return null // No option selected
        }
        val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
        return selectedRadioButton.text.toString().equals("Yes", ignoreCase = true)
    }
}
