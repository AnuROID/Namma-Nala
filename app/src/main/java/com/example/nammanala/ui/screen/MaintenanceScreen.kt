package com.example.nammanala.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border

// ─── Color tokens (shared palette) ───────────────────────────────────────────
private val NavyDeep   = Color(0xFF0D1B2A)
private val TealDark   = Color(0xFF0077A8)
private val AquaLight  = Color(0xFFCAF0F8)
private val DarkBg = Color(0xFF07111A)
private val CardBg = Color(0xFF101B2D)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF2EC4B6)
private val SoftWhite = Color(0xFFEAF4FF)
private val BorderDark = Color(0xFF1D2A3A)
private val WarnAmber = Color(0xFFF4A261)
private val DangerRed = Color(0xFFE63946)
// ─── Status config ────────────────────────────────────────────────────────────
private data class StatusConfig(
    val color: Color,
    val icon: ImageVector,
    val bg: Color
)

private fun statusConfig(status: String): StatusConfig = when {
    status.contains("Completed", ignoreCase = true) ->
        StatusConfig(Color(0xFF2EC4B6), Icons.Filled.CheckCircle, Color(0xFF2EC4B6).copy(0.1f))
    status.contains("Progress", ignoreCase = true) ->
        StatusConfig(WarnAmber, Icons.Filled.Refresh, WarnAmber.copy(0.1f))
    status.contains("Scheduled", ignoreCase = true) ->
        StatusConfig(Color(0xFF0077A8), Icons.Filled.DateRange, Color(0xFF0077A8).copy(0.1f))
    else ->
        StatusConfig(Color.Gray, Icons.Filled.Info, Color.Gray.copy(0.1f))
}

// ─── Data model ───────────────────────────────────────────────────────────────
data class MaintenanceItem(
    val section: String,
    val status: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(onBack: () -> Unit) {

    BackHandler {
        onBack()
    }

    val maintenanceList = listOf(
        MaintenanceItem("Sector A Canal",      "Cleaning Scheduled",       "Tomorrow"),
        MaintenanceItem("Sector B Canal",      "Silt Removal In Progress", "Today"),
        MaintenanceItem("Sector C Canal",      "Maintenance Completed",    "Yesterday"),
        MaintenanceItem("Tail-End Canal Section", "Inspection Scheduled",  "Next Week")
    )

    // Summary counts
    val completed  = maintenanceList.count { it.status.contains("Completed", ignoreCase = true) }
    val inProgress = maintenanceList.count { it.status.contains("Progress",  ignoreCase = true) }
    val scheduled  = maintenanceList.count { it.status.contains("Scheduled", ignoreCase = true) }

    Scaffold(
        topBar = {

            TopAppBar(
                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            "Maintenance Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            "Canal upkeep overview",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AquaLight.copy(alpha = 0.75f)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
            )
        },
        containerColor = DarkBg
    ) { paddingValues ->



        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // ── Summary strip ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryChip("Done",    "$completed",  Color(0xFF2EC4B6), Modifier.weight(1f))
                    SummaryChip("Active",  "$inProgress", WarnAmber,         Modifier.weight(1f))
                    SummaryChip("Planned", "$scheduled",  TealDark,          Modifier.weight(1f))
                }
            }

            // ── Section header ─────────────────────────────────────────────
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Build, null, tint = TealDark, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "All Tasks",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite
                        )
                    )
                }
            }

            // ── Maintenance cards ──────────────────────────────────────────
            items(maintenanceList) { item ->
                MaintenanceCard(item = item)
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
        }
    }
}

@Composable
private fun MaintenanceCard(item: MaintenanceItem) {
    val cfg = statusConfig(item.status)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),

        elevation = CardDefaults.cardElevation(2.dp),
//        modifier = Modifier.fillMaxWidth()
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                BorderDark,
                RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cfg.bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(cfg.icon, null, tint = cfg.color, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.section,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SoftWhite


                    )
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = item.status,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                )
            }

            // Date badge
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = cfg.bg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = cfg.color,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}