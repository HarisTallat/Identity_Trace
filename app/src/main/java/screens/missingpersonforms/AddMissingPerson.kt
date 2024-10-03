package screens.missingpersonforms

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.identity.trace.R
import screens.dashboard.DashboardActivity
import java.util.*

class AddMissingPersonActivity : ComponentActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextLastKnownLocation: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editTextMissingDate: EditText
    private lateinit var buttonUploadImage: Button
    private lateinit var buttonSubmit: Button
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_missing_person_form)
        initUI();

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data ?: return@registerForActivityResult
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
        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        editTextLastKnownLocation = findViewById(R.id.editTextLastKnownLocation)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        editTextMissingDate = findViewById(R.id.editTextMissingDate)
        buttonUploadImage = findViewById(R.id.buttonUploadImage)
        buttonSubmit = findViewById(R.id.buttonSubmit)
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

        if (name.isEmpty() || age.isEmpty() || lastKnownLocation.isEmpty() || missingDate.isEmpty() || gender == null || !::selectedImageUri.isInitialized) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().reference
        val userId = database.push().key

        if (userId != null) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageReference = storageReference.child("missing_person_images/$userId.jpg")

            imageReference.putFile(selectedImageUri)
                .addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener { uri ->
                        saveUserDetails(userId, name, age, lastKnownLocation, missingDate, gender, uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error generating user ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDetails(userId: String, name: String, age: String, lastKnownLocation: String, missingDate: String, gender: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance().reference

        val user = mapOf(
            "name" to name,
            "age" to age,
            "lastKnownLocation" to lastKnownLocation,
            "missingDate" to missingDate,
            "gender" to gender,
            "imageUrl" to imageUrl
        )

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Details submitted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit details", Toast.LENGTH_SHORT).show()
            }
    }

}