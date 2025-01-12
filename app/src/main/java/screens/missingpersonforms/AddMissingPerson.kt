package screens.missingpersonforms

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.sun.mail.iap.Response
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import screens.dashboard.DashboardActivity
import screens.record_status.MissingPersonRecordStatus
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
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

        // Validate inputs
        if (name.isEmpty() || !name.matches(Regex("^[a-zA-Z\\s]+\$"))) {
            Toast.makeText(this, "Name should only contain letters and spaces", Toast.LENGTH_SHORT).show()
            return
        }

        if (age.isEmpty() || !age.matches(Regex("^\\d+\$"))) {
            Toast.makeText(this, "Age should be a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        if (lastKnownLocation.isEmpty() || !lastKnownLocation.matches(Regex("^[a-zA-Z0-9\\s,]+\$"))) {
            Toast.makeText(this, "Last Known Location should be a valid string", Toast.LENGTH_SHORT).show()
            return
        }

        if (missingDate.isEmpty()) {
            Toast.makeText(this, "Missing date should be a valid date", Toast.LENGTH_SHORT).show()
            return
        }


        if (gender == null || !::selectedImageUri.isInitialized) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().reference
        val userId = database.push().key

        if (userId != null) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageReference = storageReference.child("missing_person_images/$userId")

            imageReference.putFile(selectedImageUri)
                .addOnSuccessListener {
                    callSearchImageApi(this, selectedImageUri) { isFound ->
                        if (isFound) {
                            Toast.makeText(
                                this, "This image already exists in your system", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            Handler(Looper.getMainLooper()).postDelayed({
                                finish() // This will go back to the previous activity after 3 seconds
                            }, 3000)
                        } else {
                            imageReference.downloadUrl.addOnSuccessListener { uri ->
                                saveUserDetails(userId, name, age, lastKnownLocation, missingDate, gender, uri.toString())
                            }
                            callAddImageApi(selectedImageUri, userId)
                        }
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error generating user ID", Toast.LENGTH_SHORT).show()
        }
    }



    private fun callAddImageApi(imageUri: Uri, imageId: String) {
        Log.d("API_CALL", "callAddImageApi invoked with imageUri: $imageUri and imageId: $imageId")

        val contentResolver = contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        Log.d("API_CALL", "InputStream: $inputStream")

        if (inputStream != null) {
            try {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "image",
                        imageId,
                        inputStream.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("imageId", imageId)
                    .build()
                Log.d("API_CALL", "Request body created successfully")

                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/add_image") // Check the URL and your Flask server setup
                    .post(requestBody)
                    .build()
                Log.d("API_CALL", "Request built: $request")

                val client = OkHttpClient()
                Log.d("API_CALL", "OkHttpClient initialized")

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("API_CALL", "API call failed", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@AddMissingPersonActivity,
                                "API call failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResponse(call: Call, response: okhttp3.Response) {
                        Log.d("API_CALL", "Response received: $response")
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Log.d("API_CALL", "API call successful")
                                Toast.makeText(
                                    this@AddMissingPersonActivity,
                                    "Details saved successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@AddMissingPersonActivity,
                                        DashboardActivity::class.java
                                    )
                                )
                            } else {
                                Log.e("API_CALL", "Error response code: ${response.code}")
                                Toast.makeText(
                                    this@AddMissingPersonActivity,
                                    "Error: ${response.code}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                })
            } catch (e: Exception) {
                Log.e("API_CALL", "Exception while building request body or executing API call", e)
            }
        } else {
            Log.e("API_CALL", "Failed to resolve image URI")
            Toast.makeText(
                this@AddMissingPersonActivity,
                "Failed to resolve image URI",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun callSearchImageApi(context: Context, imageUri: Uri, callback: (Boolean) -> Unit) {
        Log.d("API_CALL", "callSearchImageApi invoked with imageUri: $imageUri")

        val contentResolver = context.contentResolver // Use the passed context
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        Log.d("API_CALL", "InputStream: $inputStream")

        if (inputStream != null) {
            try {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "image",
                        "image.jpg", // Specify a filename
                        inputStream.asRequestBody("image/*".toMediaTypeOrNull())
                    ).build()
                Log.d("API_CALL", "Request body created successfully")

                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/check_image")
                    .post(requestBody)
                    .build()
                Log.d("API_CALL", "Request built: $request")

                val client = OkHttpClient()
                Log.d("API_CALL", "OkHttpClient initialized")

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("API_CALL", "API call failed", e)
                        (context as Activity).runOnUiThread {
                            Toast.makeText(
                                context,
                                "API call failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            callback(false) // Return false in case of failure
                        }
                    }

                    override fun onResponse(call: Call, response: okhttp3.Response) {
                        Log.d("API_CALL", "Response received: $response")
                        (context as Activity).runOnUiThread {
                            if (response.isSuccessful) {
                                callback(true) // Return true if the image is found
                            } else {
                                callback(false) // Return false if the image is not found
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("API_CALL", "Exception while building request body or executing API call", e)
                Toast.makeText(
                    context,
                    "An error occurred: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false) // Return false in case of an exception
            }
        } else {
            Log.e("API_CALL", "Failed to resolve image URI")
            Toast.makeText(
                context,
                "Failed to resolve image URI",
                Toast.LENGTH_SHORT
            ).show()
            callback(false) // Return false if the URI cannot be resolved
        }
    }



    // Extension function to convert InputStream to RequestBody
    private fun InputStream.asRequestBody(mediaType: MediaType?): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return mediaType
            }

            override fun writeTo(sink: BufferedSink) {
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (read(buffer).also { bytesRead = it } != -1) {
                    sink.write(buffer, 0, bytesRead)
                }
            }
        }
    }


    // Extension function to convert File to RequestBody
    private fun File.asRequestBody(mediaType: MediaType?): RequestBody = RequestBody.create(mediaType, this)
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