package com.example.rodexapp.ui.inspection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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
        val roadSurfaceType = findViewById<EditText>(R.id.roadSurfaceTypeEditText).text.toString()

        val inspectionData = InspectionRequest(
            name_of_officer = inspectorName,
            name_of_road = roadName,
            length_of_road = roadLength,
            width_of_road = roadSectionWidth,
            type_of_road_surface = roadSurfaceType,
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

    private fun getStaticMapUrl(lat: String, lng: String): String {
        val apiKey = "AIzaSyB720rKZAHxV5FHVp8PodVeAlERLNM_0k4"
        return "https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&zoom=16&size=600x300&maptype=roadmap&markers=color:red%7C$lat,$lng&key=$apiKey"
    }

    private fun fetchMapImage(url: String, callback: (Bitmap?) -> Unit) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                callback(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }.start()
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
                            val locationParts = inspectionResponse.location_start.split(",")
                            val mapUrl = getStaticMapUrl(locationParts[0], locationParts[1])
                            fetchMapImage(mapUrl) { mapImage ->
                                goToReportPage(mapImage)
                            }
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
                                val locationParts = inspectionResponse.location_start.split(",")
                                val mapUrl = getStaticMapUrl(locationParts[0], locationParts[1])
                                fetchMapImage(mapUrl) { mapImage ->
                                    goToReportPage(mapImage)
                                }
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
            withContext(Dispatchers.IO) {
                try {
                    val response = ApiClient.damageService.upload(damageRequest)
                    withContext(Dispatchers.Main) {
                        callback(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()  // Print stack trace for debugging
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FormInspectionAdminActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                        callback(null)
                    }
                }
            }
        }
    }

    private fun goToReportPage(mapImage: Bitmap?) {
        if (!::damageResponse.isInitialized) {
            Toast.makeText(this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, ReportPageAdminActivity::class.java)

        // Pass the image data as an extra. You need to handle this appropriately in the ReportActivity
        if (mapImage != null) {
            intent.putExtra("map_image_uri", saveBitmapToFile(mapImage).toString())
        }

        intent.putExtra("report_id", inspectionResponse.id)
        intent.putExtra("report_name", inspectionResponse.name_of_road)
        intent.putExtra("report_length", inspectionResponse.length_of_road)
        intent.putExtra("report_lat_lon", inspectionResponse.location_start)
        intent.putExtra("report_surface_type", inspectionResponse.type_of_road_surface)
        intent.putExtra("report_image_url", damageResponse.image_url)
        intent.putExtra("alligator_cracking", damageResponse.count_damages_type_0)
        intent.putExtra("lateral_cracking", damageResponse.count_damages_type_1)
        intent.putExtra("longitudinal_crack", damageResponse.count_damages_type_2)
        intent.putExtra("pothole", damageResponse.count_damages_type_3)
        intent.putExtra("count_damages", damageResponse.count_damages)

        startActivity(intent)
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        return try {
            val file = File(applicationContext.cacheDir, "map.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
