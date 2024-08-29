package com.example.rodexapp.ui.report

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import com.example.rodexapp.R
import com.example.rodexapp.ui.auth.LoginActivity
import com.example.rodexapp.ui.inspection.FormInspectionActivity
import com.example.rodexapp.ui.inspection.FormInspectionAdminActivity

class AboutRoadAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_road_admin)

        findViewById<Button>(R.id.inspector_button).setOnClickListener {
            val intent = Intent(this, FormInspectionAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.carbon_report_button).setOnClickListener {
            val intent = Intent(this, ListReportAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
