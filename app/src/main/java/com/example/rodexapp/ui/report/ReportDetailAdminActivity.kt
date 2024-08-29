package com.example.rodexapp.ui.report

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.rodexapp.R
import com.example.rodexapp.ui.inspection.FormInspectionActivity
import com.example.rodexapp.ui.inspection.FormInspectionAdminActivity

class ReportDetailAdminActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail_admin)

        val imageView: ImageView = findViewById(R.id.detailImageView)
        val nameView: TextView = findViewById(R.id.detailNameTextView)
        val lengthView: TextView = findViewById(R.id.detailLengthTextView)
        val latLonView: TextView = findViewById(R.id.detailLatLonTextView)
        val surfaceTypeView: TextView = findViewById(R.id.detailSurfaceTypeTextView)
        val countDamagesView: TextView = findViewById(R.id.totalDamageTextView)
        val alligatorCrackingView: TextView = findViewById(R.id.alligatorCrackingTextView)
        val lateralCrackingView: TextView = findViewById(R.id.lateralCrackingTextView)
        val longitudinalCrackView: TextView = findViewById(R.id.longitudinalCrackTextView)
        val potholeView: TextView = findViewById(R.id.potholeTextView)

        val intent = intent
        val name = intent.getStringExtra("report_name")
        val length = intent.getStringExtra("report_length")
        val latLon = intent.getStringExtra("report_lat_lon")
        val surfaceType = intent.getStringExtra("report_surface_type")
        val imageUrl = intent.getStringExtra("report_image_url")
        val countDamages = intent.getStringExtra("count_damages")
        val alligatorCracking = intent.getStringExtra("alligator_cracking")
        val lateralCracking = intent.getStringExtra("lateral_cracking")
        val longitudinalCrack = intent.getStringExtra("longitudinal_crack")
        val pothole = intent.getStringExtra("pothole")

        // Set data to views
        nameView.text = name
        lengthView.text = "Road length: $length"
        latLonView.text = "Lat, Lon: $latLon"
        surfaceTypeView.text = "Surface type: $surfaceType"

        // Set damage counts to views
        countDamagesView.text = "Total Damages: $countDamages"
        alligatorCrackingView.text = "Alligator Cracking: $alligatorCracking"
        lateralCrackingView.text = "Lateral Cracking: $lateralCracking"
        longitudinalCrackView.text = "Longitudinal Crack: $longitudinalCrack"
        potholeView.text = "Pothole: $pothole"

        // Load image using Glide
        Glide.with(this).load(imageUrl).into(imageView)

        findViewById<Button>(R.id.inspector_button).setOnClickListener {
            val intent = Intent(this, FormInspectionAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.report_button).setOnClickListener {
            val intent = Intent(this, AboutRoadAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, ListReportAdminActivity::class.java)
            startActivity(intent)
        }
    }
}
