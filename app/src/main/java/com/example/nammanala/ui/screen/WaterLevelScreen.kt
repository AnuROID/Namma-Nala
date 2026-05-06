package com.example.nammanala.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.automirrored.filled.ArrowBack

// ─── Palette ──────────────────────────────────────────────────────────────────
private val NavyDeep  = Color(0xFF0D1B2A)
private val TealDark  = Color(0xFF0077A8)
private val AquaLight = Color(0xFFCAF0F8)
private val DarkBg = Color(0xFF07111A)
private val CardBg = Color(0xFF101B2D)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF2EC4B6)
private val SoftWhite = Color(0xFFEAF4FF)
private val BorderDark = Color(0xFF1D2A3A)
private val WarnAmber = Color(0xFFF4A261)
private val DangerRed = Color(0xFFE63946)
// ─── Data model ───────────────────────────────────────────────────────────────
data class WaterLevelItem(
    val section: String,
    val level: String,
    val percentage: Int
)

private fun levelColor(pct: Int): Color = when {
    pct >= 75 -> Color(0xFF2EC4B6)
    pct >= 40 -> Color(0xFFF4A261)
    else       -> Color(0xFFE63946)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterLevelScreen( onBack: () -> Unit) {
    BackHandler {
        onBack()
    }

    val waterLevels = listOf(
        WaterLevelItem("Sector A Canal",   "High Flow",     90),
        WaterLevelItem("Sector B Canal",   "Medium Flow",   65),
        WaterLevelItem("Sector C Canal",   "Low Flow",      30),
        WaterLevelItem("Tail-End Canal",   "Critical Low",  10)
    )

    val avgLevel = waterLevels.map { it.percentage }.average().toInt()

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
                            "Water Level Monitor",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            "Real-time flow readings",
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

            // ── Average gauge card ─────────────────────────────────────────
            item {
                AverageFlowCard(avgPercent = avgLevel)
            }

            // ── Section label ──────────────────────────────────────────────
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Waves, null, tint = TealDark, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Canal Sectors",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold, color = SoftWhite
                        )
                    )
                }
            }

            // ── Water level cards ──────────────────────────────────────────
            items(waterLevels) { item ->
                WaterLevelCard(item = item)
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun AverageFlowCard(avgPercent: Int) {
    val color = levelColor(avgPercent)
    val animatedProgress by animateFloatAsState(
        targetValue = avgPercent / 100f,
        animationSpec = tween(1000),
        label = "avg_progress"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDeep),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "System Average Flow",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = SoftWhite.copy(0.7f)
                )
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "$avgPercent%",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 52.sp
                )
            )
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.White.copy(alpha = 0.8f),
                strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(6.dp))
            Text(
                when {
                    avgPercent >= 75 -> "✅ Good — Canals flowing well"
                    avgPercent >= 40 -> "⚠️ Moderate — Monitor closely"
                    else              -> "🚨 Alert — Multiple critical sections"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color
                )
            )
        }
    }
}

@Composable
private fun WaterLevelCard(item: WaterLevelItem) {
    val color = levelColor(item.percentage)
    val animatedProgress by animateFloatAsState(
        targetValue = item.percentage / 100f,
        animationSpec = tween(800),
        label = "progress_${item.section}"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Percentage circle indicator
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.percentage}%",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = color
                    )
                )
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
                    text = item.level,
                    style = MaterialTheme.typography.bodySmall.copy(color = color)
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}