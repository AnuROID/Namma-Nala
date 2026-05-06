package com.example.nammanala.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.nammanala.data.Report
import com.example.nammanala.repository.ReportRepository

class ReportViewModel : ViewModel() {

    private val repo = ReportRepository()

    var reports = mutableStateOf<List<Report>>(emptyList())
        private set

    // 🔥 Fetch all reports
    fun fetchData() {
        repo.fetchReports {
            reports.value = it
        }
    }

    // 🔥 Upload image + save report
    fun uploadImage(
        bitmap: Bitmap,
        type: String,
        latitude: Double,
        longitude: Double
    ) {
        repo.uploadImageAndSaveReport(
            bitmap,
            type = type,
            latitude = latitude,
            longitude = longitude,
            onSuccess = {

                Log.d(
                    "Upload",
                    "Report saved successfully"
                )

                fetchData()

                Log.d(
                    "Firestore Reports",
                    reports.value.toString()
                )
            },
            onFailure = {
                Log.e("Upload", "Error", it)
            }
        )
    }
}