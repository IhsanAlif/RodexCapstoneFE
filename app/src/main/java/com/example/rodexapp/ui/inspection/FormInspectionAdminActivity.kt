package com.example.rodexapp.ui.inspection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.rodexapp.R
import com.example.rodexapp.api.ApiClient
import com.example.rodexapp.model.Damage
import com.example.rodexapp.model.DamageRequest
import com.example.rodexapp.model.Inspection
import com.example.rodexapp.model.InspectionRequest
import com.example.rodexapp.ui.report.AboutRoadAdminActivity
import com.example.rodexapp.ui.report.ListReportAdminActivity
import com.example.rodexapp.ui.report.ReportPageAdminActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.ByteArrayOutputStream

class FormInspectionAdminActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var inspectionResponse: Inspection
    private lateinit var damageResponse: Damage

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private val LOCATION_REQUEST_CODE = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_inspection_admin)

        // Initialize the Spinner
        val roadSurfaceTypeSpinner = findViewById<Spinner>(R.id.roadSurfaceTypeSpinner)
        val roadSurfaceClassSpinner = findViewById<Spinner>(R.id.roadSurfaceClassSpinner)

        // List of road surface types
        val roadSurfaceTypes = listOf(
            "National Road",
            "Provincial Road",
            "Regency Road",
            "City Road",
            "Village Road"
        )

        val roadSurfaceClasses = listOf(
            "Class I Road",
            "Class II Road",
            "Class III Road",
            "Special Road"
        )

        // Set up the ArrayAdapter for the Spinner
        val adapterTypes = ArrayAdapter(this, android.R.layout.simple_spinner_item, roadSurfaceTypes)
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roadSurfaceTypeSpinner.adapter = adapterTypes

        val adapterClasses = ArrayAdapter(this, android.R.layout.simple_spinner_item, roadSurfaceClasses)
        adapterClasses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roadSurfaceClassSpinner.adapter = adapterClasses

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val startInspectionButton = findViewById<Button>(R.id.startInspectionButton)

        startInspectionButton.setOnClickListener {
            getCurrentLocation { location ->
                val locationStart = "${location.latitude},${location.longitude}"

                // Send inspection data to backend
                sendInspectionData(locationStart) { success, response ->
                    if (success && response != null) {
                        inspectionResponse = response
                        showImagePickerOptions()
                    } else {
                        Toast.makeText(this@FormInspectionAdminActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        findViewById<Button>(R.id.report_button).setOnClickListener {
            val intent = Intent(this, AboutRoadAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.carbon_report_button).setOnClickListener {
            val intent = Intent(this, ListReportAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, AboutRoadAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendInspectionData(locationStart: String, callback: (Boolean, Inspection?) -> Unit) {
        val inspectorName = findViewById<EditText>(R.id.inspectorNameEditText).text.toString()
        val roadName = findViewById<EditText>(R.id.roadNameEditText).text.toString()
        val roadLength = findViewById<EditText>(R.id.roadLengthEditText).text.toString()
        val roadSectionWidth = findViewById<EditText>(R.id.roadSectionWidthEditText).text.toString()
        val roadSurfaceType = findViewById<Spinner>(R.id.roadSurfaceTypeSpinner).selectedItem.toString()
        val roadSurfaceClass = findViewById<Spinner>(R.id.roadSurfaceClassSpinner).selectedItem.toString()
        val roadSurfaceAddress = findViewById<EditText>(R.id.roadAddressEditText).text.toString()
        val additionalInformation = findViewById<EditText>(R.id.additionalInformationEditText).text.toString()

        val inspectionData = InspectionRequest(
            name_of_officer = inspectorName,
            name_of_road = roadName,
            length_of_road = roadLength,
            width_of_road = roadSectionWidth,
            type_of_road_surface = roadSurfaceType,
            road_class = roadSurfaceClass,
            address = roadSurfaceAddress,
            additional = additionalInformation,
            location_start = locationStart
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = ApiClient.inspectionService.new(inspectionData)
                    withContext(Dispatchers.Main) {
                        callback(true, response)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        callback(false, null)
                    }
                }
            }
        }
    }

    private fun getCurrentLocation(callback: (Location) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    if (location != null) {
                        callback(location)
                    } else {
                        // Handle location being null
                        Toast.makeText(this@FormInspectionAdminActivity, "Unexpected error occurred Location", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle location retrieval failure
                    Toast.makeText(this@FormInspectionAdminActivity, "Unexpected error occurred Location", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    val base64Image = encodeToBase64(photo)
                    val damageRequest = DamageRequest(
                        file = base64Image,
                        identifier = "fileName",
                        inspectionId = inspectionResponse.id
                    )
                    uploadImageToBackend(damageRequest) { damage ->
                        if (damage != null) {
                            damageResponse = damage
                            goToReportPage()
                        }
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImage: Uri? = data?.data
                    // Convert URI to Bitmap, encode to base64, and upload
                    if (selectedImage != null) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        val base64Image = encodeToBase64(bitmap)
                        val damageRequest = DamageRequest(
                            file = base64Image,
                            identifier = "fileName",
                            inspectionId = inspectionResponse.id
                        )
                        uploadImageToBackend(damageRequest) { damage ->
                            if (damage != null) {
                                damageResponse = damage
                                goToReportPage()
                            }
                        }
                    }
                }
            }
        }
    }

    // Function to encode Bitmap to Base64
    private fun encodeToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Function to upload Base64 encoded image to backend
    private fun uploadImageToBackend(damageRequest: DamageRequest, callback: (Damage?) -> Unit) {
        lifecycleScope.launch {
            withTimeout(15_000) { // 15-second timeout
                withContext(Dispatchers.IO) {
                    try {
                        val response = ApiClient.damageService.upload(damageRequest)
                        withContext(Dispatchers.Main) {
                            damageResponse = response
                            callback(response)
                            showPopupMessage(success = true)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()  // Print stack trace for debugging
                        withContext(Dispatchers.Main) {
                            showPopupMessage(success = false)
                            callback(null)
                        }
                    }
                }
            }
        }
    }

    private fun showPopupMessage(success: Boolean) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Upload Status")

        builder.setMessage("Thank you for uploading.")

        if (success) {
            builder.setPositiveButton("OK") { _, _ ->
                goToReportPage() // Navigate to report page on success
            }
        } else {
            builder.setPositiveButton("OK") { _, _ ->
                goToHomePage() // Navigate to home page on failure
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun goToHomePage() {
        val intent = Intent(this, AboutRoadAdminActivity::class.java) // Home page
        startActivity(intent)
    }

    private fun goToReportPage() {
        if (!::damageResponse.isInitialized) {
            Toast.makeText(this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, ReportPageAdminActivity::class.java)

        // Pass the image data as an extra. You need to handle this appropriately in the ReportActivity
        intent.putExtra("report_id", inspectionResponse.id)
        intent.putExtra("report_name", inspectionResponse.name_of_road)
        intent.putExtra("report_length", inspectionResponse.length_of_road)
        intent.putExtra("report_lat_lon", inspectionResponse.location_start)
        intent.putExtra("report_road_type", inspectionResponse.type_of_road_surface)
        intent.putExtra("report_road_class", inspectionResponse.road_class)
        intent.putExtra("report_road_address", inspectionResponse.address)
        intent.putExtra("report_road_additional", inspectionResponse.additional)
        intent.putExtra("report_road_inspection_date", inspectionResponse.date_started)
        intent.putExtra("report_image_url", damageResponse.image_url)
        intent.putExtra("alligator_cracking", damageResponse.count_damages_type_0)
        intent.putExtra("lateral_cracking", damageResponse.count_damages_type_1)
        intent.putExtra("longitudinal_crack", damageResponse.count_damages_type_2)
        intent.putExtra("pothole", damageResponse.count_damages_type_3)
        intent.putExtra("count_damages", damageResponse.count_damages)

        startActivity(intent)
    }
}
