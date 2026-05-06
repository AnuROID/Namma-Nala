package com.example.nammanala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nammanala.ui.MainScreen
import com.example.nammanala.ui.screen.MapScreen
import com.example.nammanala.ui.screen.MaintenanceScreen
import com.example.nammanala.ui.screen.WaterLevelScreen
import com.example.nammanala.ui.screen.WaterStatusScreen
import com.example.nammanala.viewmodel.ReportViewModel
import com.example.nammanala.viewmodel.WaterStatusViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val reportVm: ReportViewModel = viewModel()

            val waterVm: WaterStatusViewModel = viewModel()

            // 🔥 Screen states
            var showMap by remember {
                mutableStateOf(false)
            }

            var showWaterStatus by remember {
                mutableStateOf(false)
            }

            var showMaintenance by remember {
                mutableStateOf(false)
            }

            var showWaterLevel by remember {
                mutableStateOf(false)
            }

            // 🔥 Fetch data initially
            LaunchedEffect(Unit) {

                reportVm.fetchData()

                waterVm.fetchStatus()
            }

            // 🗺️ MAP SCREEN



            if (showMap) {

                MapScreen(
                    reports = reportVm.reports.value,
                    onBack = {
                        showMap = false
                    }
                )
            }

            // 💧 WATER STATUS SCREEN
            else if (showWaterStatus) {

                WaterStatusScreen(
                    onBack = {
                        showWaterStatus = false
                    },

                    waterStatusList =
                        waterVm.waterStatusList.value,

                    onAddStatus = { village ->

                        waterVm.addStatus(village)
                    }

                )

            }

            // 🛠️ MAINTENANCE SCREEN
            else if (showMaintenance) {

                MaintenanceScreen(onBack = {
                    showMaintenance = false
                })
            }

            // 🌊 WATER LEVEL SCREEN
            else if (showWaterLevel) {

                WaterLevelScreen( onBack = {
                    showWaterLevel = false
                })
            }

            // 🏠 MAIN SCREEN
            else {

                MainScreen(

                    reports = reportVm.reports.value,

                    onFetchClick = {
                        reportVm.fetchData()
                    },

                    onImageCaptured = {
                            bitmap,
                            lat,
                            lng,
                            type ->

                        reportVm.uploadImage(
                            bitmap = bitmap,
                            type = type,
                            latitude = lat,
                            longitude = lng
                        )
                    },

                    onOpenMap = {
                        showMap = true
                    },

                    onOpenWaterStatus = {
                        showWaterStatus = true
                    },

                    onOpenMaintenance = {
                        showMaintenance = true
                    },

                    onOpenWaterLevel = {
                        showWaterLevel = true
                    }

                )
            }
        }
    }
}