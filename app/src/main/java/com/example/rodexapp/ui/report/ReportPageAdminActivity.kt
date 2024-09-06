package com.example.rodexapp.ui.report

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.rodexapp.R
import com.example.rodexapp.ui.inspection.FormInspectionAdminActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ReportPageAdminActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_page_admin)

        // Retrieve data from intent
        val name = intent.getStringExtra("report_name") ?: "Unknown"
        val length = intent.getStringExtra("report_length") ?: "Unknown"
        val latLon = intent.getStringExtra("report_lat_lon") ?: "Unknown"
        val roadType = intent.getStringExtra("report_road_type") ?: "Unknown"
        val roadClass = intent.getStringExtra("report_road_class") ?: "Unknown"
        val roadAddress = intent.getStringExtra("report_road_address") ?: "Unknown"
        val roadAdditional = intent.getStringExtra("report_road_additional") ?: "Unknown"
        val roadInspectionDate = intent.getStringExtra("report_road_inspection_date") ?: "Unknown"
        val imageUrl = intent.getStringExtra("report_image_url")
        val countDamages = intent.getStringExtra("count_damages") ?: "Unknown"
        val alligatorCracking = intent.getStringExtra("alligator_cracking") ?: "Unknown"
        val lateralCracking = intent.getStringExtra("lateral_cracking") ?: "Unknown"
        val longitudinalCrack = intent.getStringExtra("longitudinal_crack") ?: "Unknown"
        val pothole = intent.getStringExtra("pothole") ?: "Unknown"

        val locationParts = latLon.split(",")
        val mapUrl = getStaticMapUrl(locationParts[0], locationParts[1])
        fetchMapImage(mapUrl) { mapImage ->
            findViewById<ImageView>(R.id.mapImageView).setImageBitmap(mapImage)
        }

        // Set data to views
        findViewById<TextView>(R.id.detailNameTextView).text = name
        findViewById<TextView>(R.id.detailLengthTextView).text = "Road length: $length"
        findViewById<TextView>(R.id.detailLatLonTextView).text = "Lat, Lon: $latLon"
        findViewById<TextView>(R.id.detailRoadTypeTextView).text = "Road type: $roadType"
        findViewById<TextView>(R.id.detailRoadClassTextView).text = "Road class: $roadClass"
        findViewById<TextView>(R.id.detailRoadAddressTextView).text = "Address: $roadAddress"
        findViewById<TextView>(R.id.detailRoadAdditionalTextView).text = "Additional Information: $roadAdditional"
        findViewById<TextView>(R.id.detailRoadDateTextView).text = "Inspection date: $roadInspectionDate"

        // Load report image from URL using Glide
        imageUrl?.let {
            val detailImageView = findViewById<ImageView>(R.id.detailImageView)
            Glide.with(this)
                .load(it)
                .into(detailImageView)
        }

        // Set damages information
        findViewById<TextView>(R.id.totalDamageTextView).text = "Total Damages: $countDamages"
        findViewById<TextView>(R.id.alligatorCrackingTextView).text = "Alligator cracking: $alligatorCracking"
        findViewById<TextView>(R.id.lateralCrackingTextView).text = "Lateral cracking: $lateralCracking"
        findViewById<TextView>(R.id.longitudinalCrackTextView).text = "Longitudinal crack: $longitudinalCrack"
        findViewById<TextView>(R.id.potholeTextView).text = "Pothole: $pothole"

        // Set up button listeners
        findViewById<Button>(R.id.inspector_button).setOnClickListener {
            val intent = Intent(this, FormInspectionAdminActivity::class.java)
            startActivity(intent)
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
            finish()
        }
    }

    private fun getStaticMapUrl(lat: String, lng: String): String {
        val apiKey = "AIzaSyB720rKZAHxV5FHVp8PodVeAlERLNM_0k4"
        return "https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&zoom=16&size=600x300&maptype=roadmap&markers=color:red%7C$lat,$lng&key=$apiKey"
    }

    private fun fetchMapImage(url: String, callback: (Bitmap?) -> Unit) {
        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connect()
                    val inputStream = connection.inputStream
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }
            callback(bitmap)
        }
    }
}
