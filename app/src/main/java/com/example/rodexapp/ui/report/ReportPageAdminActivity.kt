package com.example.rodexapp.ui.report

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.rodexapp.R
import com.example.rodexapp.ui.inspection.FormInspectionAdminActivity

class ReportPageAdminActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_page_admin)

        // Retrieve data from intent
        val mapImageUriString = intent.getStringExtra("map_image_uri")
        val mapImageUri = mapImageUriString?.let { Uri.parse(it) }
        // Load the bitmap from URI
        if (mapImageUri != null) {
            val inputStream = contentResolver.openInputStream(mapImageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Use the bitmap as needed
            findViewById<ImageView>(R.id.mapImageView).setImageBitmap(bitmap)
        }

        val name = intent.getStringExtra("report_name") ?: "Unknown"
        val length = intent.getStringExtra("report_length") ?: "Unknown"
        val latLon = intent.getStringExtra("report_lat_lon") ?: "Unknown"
        val surfaceType = intent.getStringExtra("report_surface_type") ?: "Unknown"
        val imageUrl = intent.getStringExtra("report_image_url")
        val countDamages = intent.getStringExtra("count_damages") ?: "Unknown"
        val alligatorCracking = intent.getStringExtra("alligator_cracking") ?: "Unknown"
        val lateralCracking = intent.getStringExtra("lateral_cracking") ?: "Unknown"
        val longitudinalCrack = intent.getStringExtra("longitudinal_crack") ?: "Unknown"
        val pothole = intent.getStringExtra("pothole") ?: "Unknown"

        // Set data to views
        findViewById<TextView>(R.id.detailNameTextView).text = name
        findViewById<TextView>(R.id.detailLengthTextView).text = "Road length: $length"
        findViewById<TextView>(R.id.detailLatLonTextView).text = "Lat, Lon: $latLon"
        findViewById<TextView>(R.id.detailSurfaceTypeTextView).text = "Surface type: $surfaceType"

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
}
