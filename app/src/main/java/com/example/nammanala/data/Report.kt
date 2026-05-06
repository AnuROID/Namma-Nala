package com.example.nammanala.data

data class Report(
    val type: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val timestamp: Long = 0L
)