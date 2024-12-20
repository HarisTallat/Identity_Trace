package screens.missingpersonforms

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.identity.trace.R
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
import java.util.Calendar


class SearchMissingPersonActivity: ComponentActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextLastKnownLocation: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editTextMissingDate: EditText
    private lateinit var buttonUploadImage: Button
    private lateinit var buttonSubmit: Button
    private lateinit var selectedImageUri: Uri
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_missing_person_form)
        initUI();

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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


        callSearchImageApi(this,selectedImageUri)

    }

}

private fun callSearchImageApi(context: Context, imageUri: Uri) {
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
                    }
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    Log.d("API_CALL", "Response received: $response")
                    (context as Activity).runOnUiThread {
                        if (response.isSuccessful) {
                            Log.d("API_CALL", "API call successful, image found")
                            Toast.makeText(
                                context,
                                "Image found in our records!",
                                Toast.LENGTH_SHORT
                            ).show()
                            context.startActivity(
                                Intent(context, DashboardActivity::class.java)
                            )
                            // Pass "check: true" to the next activity
                            val intent = Intent(context, MissingPersonRecordStatus::class.java)
                            intent.putExtra("check", true)
                            context.startActivity(intent)
                        } else {
                            Log.e("API_CALL", "Image not found, response code: ${response.code}")
                            Toast.makeText(
                                context,
                                "Image not found!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Pass "check: false" to the next activity
                            val intent = Intent(context, MissingPersonRecordStatus::class.java)
                            intent.putExtra("check", false)
                            context.startActivity(intent)
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
            context,
            "Failed to resolve image URI",
            Toast.LENGTH_SHORT
        ).show()
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