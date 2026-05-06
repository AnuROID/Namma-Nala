package com.example.nammanala.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.nammanala.data.WaterStatus
import com.example.nammanala.repository.WaterStatusRepository

class WaterStatusViewModel : ViewModel() {

    private val repo = WaterStatusRepository()

    var waterStatusList =
        mutableStateOf<List<WaterStatus>>(emptyList())
        private set

    // 🔥 Add status
    fun addStatus(village: String) {

        repo.addWaterStatus(
            village = village,

            onSuccess = {
                fetchStatus()
            },

            onFailure = {

            }
        )
    }

    // 🔥 Fetch all status updates
    fun fetchStatus() {

        repo.fetchWaterStatus {

            waterStatusList.value = it
        }
    }
}