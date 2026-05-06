package com.example.nammanala.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammanala.data.Report
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// 🎨 Dark Theme Colors
private val DarkBg = Color(0xFF07111A)
private val CardBg = Color(0xFF101B2D)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF2EC4B6)
private val SoftWhite = Color(0xFFEAF4FF)
private val BorderDark = Color(0xFF1D2A3A)
private val DangerRed = Color(0xFFE63946)
private val WarnAmber = Color(0xFFF4A261)
private val Purple = Color(0xFF9B5DE5)

// 🚨 Issue Type
private data class IssueType(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

private val issueTypes = listOf(
    IssueType("Leak", Icons.Filled.Warning, DangerRed),
    IssueType("Silt", Icons.Filled.Info, WarnAmber),
    IssueType("Blockage", Icons.Filled.Close, Purple),
    IssueType("Illegal Water", Icons.Filled.Report, AccentGreen)
)

// 📍 Locality
private data class LocalityInfo(
    val name: String,
    val distanceKm: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    reports: List<Report>,
    onFetchClick: () -> Unit,
    onImageCaptured: (Bitmap, Double, Double, String) -> Unit,
    onOpenMap: () -> Unit,
    onOpenWaterStatus: () -> Unit,
    onOpenMaintenance: () -> Unit,
    onOpenWaterLevel: () -> Unit
) {

    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var capturedImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var selectedType by remember {
        mutableStateOf("Leak")
    }

    var showImagePreview by remember {
        mutableStateOf(false)
    }

    // 📸 Camera launcher
    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {

                capturedImage = bitmap
                showImagePreview = true

                getLocation(fusedLocationClient) { lat, lng ->

                    onImageCaptured(
                        bitmap,
                        lat,
                        lng,
                        selectedType
                    )
                }
            }
        }

    // 🔐 Permissions
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val cameraGranted =
                permissions[
                    Manifest.permission.CAMERA
                ] ?: false

            val locationGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] ?: false

            if (cameraGranted && locationGranted) {
                cameraLauncher.launch(null)
            }
        }

    Scaffold(

        containerColor = DarkBg,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "Namma-Nala",
                            color = SoftWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Text(
                            text = "Smart Canal Monitoring System",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentBlue
                            )
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = onFetchClick
                    ) {

                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = SoftWhite
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                )
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(18.dp),

            contentPadding = PaddingValues(bottom = 20.dp)

        ) {

            // 🚀 Hero Banner
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0B1F33),
                                    Color(0xFF003554),
                                    AccentBlue
                                )
                            )
                        )
                        .padding(22.dp)
                ) {

                    Column {

                        Surface(
                            color = AccentGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    Icons.Filled.CheckCircle,
                                    null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "LIVE SYSTEM",
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${reports.size}",
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Issues Currently Logged",
                            color = SoftWhite,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Filled.CloudDone,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Firebase Connected",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 🚨 Issue Selection
            item {

                Text(
                    text = "Select Issue Type",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(230.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(issueTypes) { type ->

                        val selected =
                            selectedType == type.label

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable {
                                    selectedType = type.label
                                }
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected)
                                        type.color
                                    else
                                        BorderDark,
                                    shape = RoundedCornerShape(20.dp)
                                ),

                            colors = CardDefaults.cardColors(
                                containerColor = CardBg
                            ),

                            shape = RoundedCornerShape(20.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),

                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    type.icon,
                                    contentDescription = null,
                                    tint = type.color,
                                    modifier = Modifier.size(28.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = type.label,
                                    color = SoftWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 📸 Report Button
            item {

                Button(
                    onClick = {

                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),

                    shape = RoundedCornerShape(20.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue
                    )
                ) {

                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = Color.Black
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Report $selectedType",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // ⚡ Quick Access
            item {

                Text(
                    text = "Quick Access",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        DarkQuickCard(
                            label = "Canal Map",
                            icon = Icons.Filled.Map,
                            color = AccentBlue,
                            onClick = onOpenMap,
                            modifier = Modifier.weight(1f)
                        )

                        DarkQuickCard(
                            label = "Water Status",
                            icon = Icons.Filled.WaterDrop,
                            color = AccentGreen,
                            onClick = onOpenWaterStatus,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        DarkQuickCard(
                            label = "Maintenance",
                            icon = Icons.Filled.Build,
                            color = WarnAmber,
                            onClick = onOpenMaintenance,
                            modifier = Modifier.weight(1f)
                        )

                        DarkQuickCard(
                            label = "Water Level",
                            icon = Icons.Filled.Waves,
                            color = Purple,
                            onClick = onOpenWaterLevel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 📷 Upload Preview
            if (capturedImage != null) {

                item {

                    AnimatedVisibility(
                        visible = showImagePreview,
                        enter = fadeIn() + slideInVertically()
                    ) {

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CardBg
                            )
                        ) {

                            Column {

                                Image(
                                    bitmap = capturedImage!!.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            null,
                                            tint = AccentGreen
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Image Uploaded Successfully",
                                            color = SoftWhite
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            showImagePreview = false
                                        }
                                    ) {

                                        Text(
                                            text = "Dismiss",
                                            color = AccentBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 📋 Recent Reports
            item {

                Text(
                    text = "Recent Reports (${reports.size})",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (reports.isEmpty()) {

                item {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = CardBg
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(44.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "No issues reported",
                                color = SoftWhite
                            )
                        }
                    }
                }

            } else {

                items(reports) { report ->

                    DarkReportCard(report)
                }
            }
        }
    }
}

@Composable
fun DarkQuickCard(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    icon,
                    contentDescription = null,
                    tint = color
                )
            }

            Text(
                text = label,
                color = SoftWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DarkReportCard(report: Report) {

    val context = LocalContext.current

    var localityInfo by remember {
        mutableStateOf<LocalityInfo?>(null)
    }

    LaunchedEffect(Unit) {

        localityInfo = resolveLocality(
            context,
            report.latitude,
            report.longitude
        )
    }

    val formattedTime = remember(report.timestamp) {

        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date(report.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    color = AccentBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = report.type,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }

                Text(
                    text = "ACTIVE",
                    color = DangerRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Filled.LocationOn,
                    null,
                    tint = AccentGreen,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = localityInfo?.name ?: "Resolving location...",
                    color = SoftWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📏 ${localityInfo?.distanceKm ?: "--"} km from nearest locality",
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⏰ $formattedTime",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

// 📍 REAL GPS
@SuppressLint("MissingPermission")
fun getLocation(
    client: FusedLocationProviderClient,
    onResult: (Double, Double) -> Unit
) {

    client.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { location ->

        if (location != null) {

            onResult(
                location.latitude,
                location.longitude
            )

        } else {

            onResult(
                23.0225,
                72.5714
            )
        }
    }
}

// 🌍 Resolve locality
private suspend fun resolveLocality(
    context: android.content.Context,
    lat: Double,
    lng: Double
): LocalityInfo? = withContext(Dispatchers.IO) {

    try {

        val geocoder = Geocoder(
            context,
            Locale.getDefault()
        )

        val addresses = geocoder.getFromLocation(
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

    } catch (e: Exception) {

        return@withContext null
    }
}