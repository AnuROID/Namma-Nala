package com.example.nammanala.repository


import com.example.nammanala.data.WaterStatus
import com.google.firebase.firestore.FirebaseFirestore

class WaterStatusRepository {

    private val db = FirebaseFirestore.getInstance()

    // 🔥 Add water status
    fun addWaterStatus(
        village: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val status = hashMapOf(

            "village" to village,

            "timestamp" to System.currentTimeMillis()
        )

        db.collection("water_status")
            .add(status)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    // 🔥 Fetch water status feed
    fun fetchWaterStatus(
        onResult: (List<WaterStatus>) -> Unit
    ) {

        db.collection("water_status")
            .get()
            .addOnSuccessListener { result ->

                val list = result.map {
                    it.toObject(WaterStatus::class.java)
                }

                onResult(list)
            }
    }
}