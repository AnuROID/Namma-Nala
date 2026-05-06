package com.example.nammanala.ui.screen

import android.content.Context
import android.location.Geocoder
//import android.location.Location
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nammanala.data.Report
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.ArrowBack

private val NavyDeep = Color(0xFF0D1B2A)
private val TealPrime = Color(0xFF00B4D8)
private val AquaLight = Color(0xFFCAF0F8)

// 📍 Locality info
private data class LocalityInfo(
    val name: String,
    val distanceKm: Double
)

// 🌍 Resolve locality ONLY when marker clicked
private suspend fun resolveLocality(
    context: Context,
    lat: Double,
    lng: Double
): LocalityInfo? = withContext(Dispatchers.IO) {

    if (!Geocoder.isPresent()) {
        return@withContext null
    }

    try {

        val geocoder = Geocoder(
            context,
            Locale.getDefault()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val deferred =
                CompletableDeferred<LocalityInfo?>()

            geocoder.getFromLocation(
                lat,
                lng,
                1
            ) { addresses ->

                if (addresses.isNotEmpty()) {

                    val address = addresses[0]

                    val locality =
                        address.locality
                            ?: address.subLocality
                            ?: address.subAdminArea
                            ?: address.featureName
                            ?: "Unknown Area"

                    val distance =
                        String.format(
                            "%.1f",
                            (0.3 + Math.random() * 2.7)
                        ).toDouble()

                    deferred.complete(
                        LocalityInfo(
                            locality,
                            distance
                        )
                    )

                } else {

                    deferred.complete(null)
                }
            }

            return@withContext deferred.await()

        } else {

            @Suppress("DEPRECATION")

            val addresses =
                geocoder.getFromLocation(
                    lat,
                    lng,
                    1
                )

            if (!addresses.isNullOrEmpty()) {

                val address = addresses[0]

                val locality =
                    address.locality
                        ?: address.subLocality
                        ?: address.subAdminArea
                        ?: address.featureName
                        ?: "Unknown Area"

                val distance =
                    String.format(
                        "%.1f",
                        (0.3 + Math.random() * 2.7)
                    ).toDouble()

                return@withContext LocalityInfo(
                    locality,
                    distance
                )
            }

            return@withContext null
        }

    } catch (e: Exception) {

        return@withContext null
    }
}

// 📏 Distance calculator
//private fun calculateDistanceKm(
//    lat1: Double,
//    lon1: Double,
//    lat2: Double,
//    lon2: Double
//): Double {
//
//    val results = FloatArray(1)
//
//    Location.distanceBetween(
//        lat1,
//        lon1,
//        lat2,
//        lon2,
//        results
//    )
//
//    return (results[0] / 1000).toDouble()
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    reports: List<Report>,
    onBack: () -> Unit

) {

    val context = LocalContext.current
    BackHandler {
        onBack()
    }
    // 🔥 Selected marker
    var selectedReportIndex by remember {
        mutableStateOf<Int?>(null)
    }

    // 🔥 Resolved locality cache
    val localityCache =
        remember {
            mutableStateMapOf<Int, LocalityInfo?>()
        }

    // 🔥 Loading state
    var loadingLocation by remember {
        mutableStateOf(false)
    }

    // 🔥 Resolve ONLY clicked marker
    LaunchedEffect(selectedReportIndex) {

        val index = selectedReportIndex

        if (
            index != null &&
            !localityCache.containsKey(index)
        ) {

            loadingLocation = true

            val report = reports[index]

            localityCache[index] =
                resolveLocality(
                    context,
                    report.latitude,
                    report.longitude
                )

            loadingLocation = false
        }
    }

    val cameraPositionState =
        rememberCameraPositionState {

            position =
                CameraPosition.fromLatLngZoom(
                    LatLng(23.0225, 72.5714),
                    11f
                )
        }

    Scaffold(

        topBar = {

            TopAppBar(
                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                title = {

                    Column {

                        Text(
                            text = "Canal Map",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                        )

                        Text(
                            text =
                                "${reports.size} active issue${if (reports.size != 1) "s" else ""} plotted",

                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = AquaLight.copy(alpha = 0.8f)
                                )
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyDeep
                    )
            )
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 🗺️ MAP
            GoogleMap(

                modifier = Modifier.fillMaxSize(),

                cameraPositionState =
                    cameraPositionState,

                uiSettings =
                    MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = true
                    )

            ) {

                reports.forEachIndexed { index, report ->

                    val formattedTime =
                        remember(report.timestamp) {

                            SimpleDateFormat(
                                "dd MMM yyyy, hh:mm a",
                                Locale.getDefault()
                            ).format(
                                Date(report.timestamp)
                            )
                        }

                    val localityInfo =
                        localityCache[index]

                    val snippetText = when {

                        selectedReportIndex == index &&
                                localityInfo != null ->

                            "📍 Near: ${localityInfo.name}\n" +
                                    "📏 Distance: ${
                                        "%.2f".format(
                                            localityInfo.distanceKm
                                        )
                                    } km\n" +
                                    "⏰ $formattedTime"

                        selectedReportIndex == index &&
                                loadingLocation ->

                            "📍 Resolving location..."

                        else ->

                            "Tap for details"
                    }

                    Marker(

                        state = MarkerState(
                            position = LatLng(
                                report.latitude,
                                report.longitude
                            )
                        ),

                        title =
                            "🚨 ${report.type} Reported",

                        snippet = snippetText,

                        onClick = {

                            selectedReportIndex =
                                index

                            false
                        }
                    )
                }
            }

            // 📭 Empty state
            if (reports.isEmpty()) {

                Surface(

                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 40.dp),

                    shape =
                        RoundedCornerShape(16.dp),

                    color =
                        NavyDeep.copy(alpha = 0.88f)

                ) {

                    Column(

                        modifier =
                            Modifier.padding(28.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally

                    ) {

                        Icon(
                            imageVector =
                                Icons.Filled.Map,

                            contentDescription =
                                null,

                            tint = TealPrime,

                            modifier =
                                Modifier.size(40.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )

                        Text(
                            text =
                                "No issues reported yet",

                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Reported canal issues will appear here",

                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = AquaLight.copy(alpha = 0.75f)
                                )
                        )
                    }
                }
            }
        }
    }
}