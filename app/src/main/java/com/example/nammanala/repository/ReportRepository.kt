package com.example.nammanala.repository

import android.graphics.Bitmap
import com.example.nammanala.data.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class ReportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun uploadImageAndSaveReport(
        bitmap: Bitmap,
        type: String,
        latitude: Double,
        longitude: Double,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->

                    val report = hashMapOf(
                        "type" to type,
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "imageUrl" to uri.toString(),
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("reports")
                        .add(report)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun fetchReports(onResult: (List<Report>) -> Unit) {
        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { it.toObject(Report::class.java) }
                onResult(list)
            }
    }
}