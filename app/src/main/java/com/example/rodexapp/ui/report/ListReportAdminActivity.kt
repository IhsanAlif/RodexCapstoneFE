package com.example.rodexapp.ui.report

import ReportAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rodexapp.R
import com.example.rodexapp.api.ApiClient
import com.example.rodexapp.ui.inspection.FormInspectionAdminActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListReportAdminActivity : ComponentActivity() {

    private lateinit var reportRecyclerView: RecyclerView
    private lateinit var reportAdapter: ReportAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_report_admin)

        reportRecyclerView = findViewById(R.id.reportRecyclerView)
        reportRecyclerView.layoutManager = LinearLayoutManager(this)
        reportAdapter = ReportAdapter { report ->
            val intent = Intent(this, ReportPageAdminActivity::class.java)
            intent.putExtra("report_id", report.id)
            intent.putExtra("report_name", report.name_of_road)
            intent.putExtra("report_length", report.length_of_road)
            intent.putExtra("report_lat_lon", report.location_start)
            intent.putExtra("report_road_type", report.type_of_road_surface)
            intent.putExtra("report_road_class", report.road_class)
            intent.putExtra("report_road_address", report.address)
            intent.putExtra("report_road_additional", report.additional)
            intent.putExtra("report_road_inspection_date", report.date_started)
            intent.putExtra("report_image_url", report.imageUrl)
            intent.putExtra("alligator_cracking", report.count_damages_type_0)
            intent.putExtra("lateral_cracking", report.count_damages_type_1)
            intent.putExtra("longitudinal_crack", report.count_damages_type_2)
            intent.putExtra("pothole", report.count_damages_type_3)
            intent.putExtra("count_damages", report.count_damages)
            startActivity(intent)
        }
        reportRecyclerView.adapter = reportAdapter

        fetchReports()

        findViewById<Button>(R.id.inspector_button).setOnClickListener {
            val intent = Intent(this, FormInspectionAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.report_button).setOnClickListener {
            val intent = Intent(this, AboutRoadAdminActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, AboutRoadAdminActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchReports() {
        lifecycleScope.launch {
            val (reports, damages) = withContext(Dispatchers.IO) {
                val reportsDeferred = async { ApiClient.inspectionService.getAll() }
                val damagesDeferred = async { ApiClient.damageService.getAll() }
                Pair(reportsDeferred.await(), damagesDeferred.await())
            }

            // Create a map of inspectionId to imageUrl
            val inspectionDamageMap = damages.groupBy { it.inspectionId }.mapValues { it.value.first() }

            // Merge the image URLs into the report objects and filter out those without an image URL
            val reportsWithImages = reports.mapNotNull { report ->
                    val damageInfo = inspectionDamageMap[report.id]
                    if (damageInfo?.image_url != null) {
                        report.imageUrl = damageInfo.image_url
                        report.count_damages_type_0 = damageInfo.count_damages_type_0
                        report.count_damages_type_1 = damageInfo.count_damages_type_1
                        report.count_damages_type_2 = damageInfo.count_damages_type_2
                        report.count_damages_type_3 = damageInfo.count_damages_type_3
                        report.count_damages = damageInfo.count_damages
                        report
                    } else {
                        null
                    }
                }.sortedByDescending { report ->
                    // Parse the `date_started` to LocalDateTime for sorting
                    LocalDateTime.parse(report.date_started, DateTimeFormatter.ISO_DATE_TIME)
            }

            // Submit the list to the adapter
            reportAdapter.submitList(reportsWithImages)
        }
    }
}
