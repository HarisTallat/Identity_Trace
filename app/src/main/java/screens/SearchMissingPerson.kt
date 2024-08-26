package screens

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.identity.trace.R
import java.util.Calendar

class SearchMissingPersonActivity: ComponentActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextLastKnownLocation: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editTextMissingDate: EditText
    private lateinit var buttonUploadImage: Button
    private lateinit var buttonSubmit: Button
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_missing_person_form)
        initUI();

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                }
            }

        buttonUploadImage.setOnClickListener {
            openGallery();
        }

        editTextMissingDate.setOnClickListener {
            showDatePickerDialog()
        }

        buttonSubmit.setOnClickListener {
            handleSubmit()
        }
    }

    private fun initUI() {
        editTextName = findViewById(R.id.editTextSearchName)
        editTextAge = findViewById(R.id.editTextAgeSearch)
        editTextLastKnownLocation = findViewById(R.id.editTextLastKnownLocationSearch)
        radioGroupGender = findViewById(R.id.radioGroupGenderSearch)
        editTextMissingDate = findViewById(R.id.editTextMissingDateSearch)
        buttonUploadImage = findViewById(R.id.buttonUploadImageSearch)
        buttonSubmit = findViewById(R.id.buttonSubmitSearch)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$dayOfMonth/${month + 1}/$year"
                editTextMissingDate.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun handleSubmit() {
        val name = editTextName.text.toString().trim()
        val age = editTextAge.text.toString().trim()
        val lastKnownLocation = editTextLastKnownLocation.text.toString().trim()
        val missingDate = editTextMissingDate.text.toString().trim()
        val selectedGenderId = radioGroupGender.checkedRadioButtonId
        val gender = findViewById<RadioButton>(selectedGenderId)?.text?.toString()

        if (name.isEmpty() || age.isEmpty() || lastKnownLocation.isEmpty() || missingDate.isEmpty() || gender == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "Details submitted successfully", Toast.LENGTH_SHORT).show()


    }

}