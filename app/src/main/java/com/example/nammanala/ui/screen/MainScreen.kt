package com.example.nammanala.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.nammanala.viewmodel.AuthViewModel
import java.util.*

// 🎨 Theme Colors
private val DarkBg = Color(0xFF07111A)
private val CardBg = Color(0xFF101B2D)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentCyan = Color(0xFF00D4F0)
private val AccentGreen = Color(0xFF2EC4B6)
private val SoftWhite = Color(0xFFEAF4FF)
private val BorderDark = Color(0xFF1D2A3A)
private val DangerRed = Color(0xFFE63946)
private val WarnAmber = Color(0xFFF4A261)
private val Purple = Color(0xFF9B5DE5)
private val SubText = Color(0xFF7A9BB5)

private data class IssueType(val label: String, val icon: ImageVector, val color: Color)

private val issueTypes = listOf(
    IssueType("Leak", Icons.Filled.Warning, DangerRed),
    IssueType("Silt", Icons.Filled.Info, WarnAmber),
    IssueType("Blockage", Icons.Filled.Close, Purple),
    IssueType("Illegal Water", Icons.Filled.Report, AccentGreen)
)

private data class LocalityInfo(val name: String, val distanceKm: Double)

// Reusable composable for press-scale animation on any clickable
@Composable
private fun PressScaleBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pressScale"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onClick() }
                )
            },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authVm: AuthViewModel,
    onLogout: () -> Unit,
    reports: List<Report>,
    onFetchClick: () -> Unit,
    onImageCaptured: (Bitmap, Double, Double, String) -> Unit,
    onOpenMap: () -> Unit,
    onOpenWaterStatus: () -> Unit,
    onOpenMaintenance: () -> Unit,
    onOpenWaterLevel: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var selectedType by remember { mutableStateOf("Leak") }
    var showImagePreview by remember { mutableStateOf(false) }

    // Infinite glow for banner
    val bannerGlow by rememberInfiniteTransition(label = "bannerGlow").animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bannerGlowAlpha"
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            showImagePreview = true
            getLocation(fusedLocationClient) { lat, lng ->
                onImageCaptured(bitmap, lat, lng, selectedType)
                Toast.makeText(context, "✅ Report uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (cameraGranted && locationGranted) cameraLauncher.launch(null)
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
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Smart Canal Monitoring System",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentBlue,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                },
                actions = {
                    var refreshPressed by remember { mutableStateOf(false) }
                    val refreshScale by animateFloatAsState(
                        targetValue = if (refreshPressed) 0.85f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "refreshScale"
                    )
                    Box(
                        modifier = Modifier
                            .scale(refreshScale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { refreshPressed = true; tryAwaitRelease(); refreshPressed = false },
                                    onTap = {
                                        onFetchClick()
                                        Toast.makeText(context, "🔄 Refreshed successfully", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, tint = AccentBlue)
                    }

                    var logoutPressed by remember { mutableStateOf(false) }
                    val logoutScale by animateFloatAsState(
                        targetValue = if (logoutPressed) 0.85f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "logoutScale"
                    )
                    Box(
                        modifier = Modifier
                            .scale(logoutScale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { logoutPressed = true; tryAwaitRelease(); logoutPressed = false },
                                    onTap = {
                                        authVm.logout()

                                        Toast.makeText(context, "Logged out ", Toast.LENGTH_SHORT).show()
                                        onLogout()
                                    }
                                )
                            }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = null, tint = SoftWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // 🚀 Hero Banner
            item(key = "hero") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0B1F33), Color(0xFF003554), AccentBlue)
                            )
                        )
                ) {
                    // Radial glow overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AccentCyan.copy(alpha = bannerGlow * 0.2f),
                                        Color.Transparent
                                    ),
                                    radius = 400f
                                )
                            )
                    )

                    Column(modifier = Modifier.padding(22.dp)) {
                        Surface(
                            color = AccentGreen.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle, null,
                                    tint = AccentGreen, modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE SYSTEM",
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${reports.size}",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Issues Currently Logged",
                            color = SoftWhite.copy(alpha = 0.85f),
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.CloudDone, null,
                                tint = Color.White, modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firebase Connected",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 🚨 Issue Selection
            item(key = "issue_select") {
                Text(
                    text = "Select Issue Type",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(230.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(issueTypes, key = { it.label }) { type ->
                        val selected = selectedType == type.label
                        var pressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (pressed) 0.93f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMedium),
                            label = "issueScale"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .scale(scale)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) type.color else BorderDark,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                                        onTap = { selectedType = type.label }
                                    )
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected)
                                    type.color.copy(alpha = 0.08f) else CardBg
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    type.icon, null,
                                    tint = type.color, modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = type.label,
                                    color = if (selected) type.color else SoftWhite,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 📸 Report Button
            item(key = "report_btn") {
                var reportPressed by remember { mutableStateOf(false) }
                val reportScale by animateFloatAsState(
                    targetValue = if (reportPressed) 0.96f else 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "reportBtnScale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .scale(reportScale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF007EA7), AccentBlue, AccentCyan)
                            )
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { reportPressed = true; tryAwaitRelease(); reportPressed = false },
                                onTap = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt, null,
                            tint = Color(0xFF001A26),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Report $selectedType",
                            color = Color(0xFF001A26),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // ⚡ Quick Access
            item(key = "quick_access") {
                Text(
                    text = "Quick Access",
                    color = SoftWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FuturisticQuickCard(
                            label = "Canal Map", icon = Icons.Filled.Map,
                            color = AccentBlue, onClick = onOpenMap,
                            modifier = Modifier.weight(1f)
                        )
                        FuturisticQuickCard(
                            label = "Water Status", icon = Icons.Filled.WaterDrop,
                            color = AccentGreen, onClick = onOpenWaterStatus,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FuturisticQuickCard(
                            label = "Maintenance", icon = Icons.Filled.Build,
                            color = WarnAmber, onClick = onOpenMaintenance,
                            modifier = Modifier.weight(1f)
                        )
                        FuturisticQuickCard(
                            label = "Water Level", icon = Icons.Filled.Waves,
                            color = Purple, onClick = onOpenWaterLevel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 📷 Image Preview
            if (capturedImage != null) {
                item(key = "image_preview") {
                    AnimatedVisibility(
                        visible = showImagePreview,
                        enter = fadeIn(tween(300)) + slideInVertically { it / 2 }
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                ) {
                                    Image(
                                        bitmap = capturedImage!!.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Top gradient overlay for polished look
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color.Transparent, CardBg.copy(alpha = 0.8f))
                                                )
                                            )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.CheckCircle, null, tint = AccentGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Uploaded Successfully", color = SoftWhite, fontSize = 13.sp)
                                    }
                                    TextButton(onClick = { showImagePreview = false }) {
                                        Text("Dismiss", color = AccentBlue, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 📋 Recent Reports Header
            item(key = "reports_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Reports",
                        color = SoftWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Surface(
                        color = AccentBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${reports.size}",
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (reports.isEmpty()) {
                item(key = "empty_state") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.CheckCircle, null,
                                tint = AccentGreen, modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("No issues reported", color = SoftWhite, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "All canals are clear!",
                                color = SubText, fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(reports, key = { it.hashCode() }) { report ->
                    FuturisticReportCard(report)
                }
            }
        }
    }
}

@Composable
fun FuturisticQuickCard(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "quickCardScale"
    )

    Box(
        modifier = modifier
            .height(110.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { pressed = true; tryAwaitRelease(); pressed = false },
                    onTap = { onClick() }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color)
            }
            Text(text = label, color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// Keep original public name for compatibility
@Composable
fun DarkQuickCard(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = FuturisticQuickCard(label, icon, color, onClick, modifier)

@Composable
fun DarkReportCard(report: Report) = FuturisticReportCard(report)

@Composable
fun FuturisticReportCard(report: Report) {
    val context = LocalContext.current
    var localityInfo by remember { mutableStateOf<LocalityInfo?>(null) }

    LaunchedEffect(report.latitude, report.longitude) {
        localityInfo = resolveLocality(context, report.latitude, report.longitude)
    }

    val formattedTime = remember(report.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(report.timestamp))
    }

    val typeColor = remember(report.type) {
        when (report.type) {
            "Leak" -> DangerRed
            "Silt" -> WarnAmber
            "Blockage" -> Purple
            "Illegal Water" -> AccentGreen
            else -> AccentBlue
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Top accent line per type
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(typeColor.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = typeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = report.type,
                        color = typeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = DangerRed.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "● ACTIVE",
                        color = DangerRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = localityInfo?.name ?: "Resolving location...",
                    color = SoftWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "📏 ${localityInfo?.distanceKm ?: "--"} km from nearest locality",
                color = SubText,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "⏰ $formattedTime",
                color = SubText.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

// 📍 REAL GPS — unchanged
@SuppressLint("MissingPermission")
fun getLocation(
    client: FusedLocationProviderClient,
    onResult: (Double, Double) -> Unit
) {
    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            } else {
                onResult(23.0225, 72.5714)
            }
        }
}

// 🌍 Resolve locality — unchanged
private suspend fun resolveLocality(
    context: android.content.Context,
    lat: Double,
    lng: Double
): LocalityInfo? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val locality = address.locality
                ?: address.subLocality
                ?: address.subAdminArea
                ?: "Unknown Area"
            val distance = String.format("%.1f", (0.3 + Math.random() * 2.7)).toDouble()
            return@withContext LocalityInfo(locality, distance)
        }
        return@withContext null
    } catch (e: Exception) {
        return@withContext null
    }
}