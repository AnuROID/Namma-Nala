package com.example.nammanala.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.nammanala.data.WaterStatus
import java.text.SimpleDateFormat
import java.util.*

// 🌑 Dark Theme Colors
private val DarkBg = Color(0xFF07111A)
private val CardBg = Color(0xFF101B2D)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentGreen = Color(0xFF2EC4B6)
private val SoftWhite = Color(0xFFEAF4FF)
private val BorderDark = Color(0xFF1D2A3A)
private val WarnAmber = Color(0xFFF4A261)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterStatusScreen(
    waterStatusList: List<WaterStatus>,
    onAddStatus: (String) -> Unit,
    onBack: () -> Unit
) {

    BackHandler {
        onBack()
    }

    var village by remember {
        mutableStateOf("")
    }

    val keyboard =
        LocalSoftwareKeyboardController.current

    fun submit() {

        if (village.isNotBlank()) {

            onAddStatus(
                village.trim()
            )

            village = ""

            keyboard?.hide()
        }
    }

    Scaffold(

        containerColor = DarkBg,

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
                            text = "Water Status Feed",

                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                        )

                        Text(
                            text = "Village-level water arrival log",

                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = SoftWhite.copy(alpha = 0.75f)
                                )
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
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

            verticalArrangement =
                Arrangement.spacedBy(14.dp),

            contentPadding =
                PaddingValues(vertical = 16.dp)

        ) {

            // 🚀 Input Section
            item {

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            BorderDark,
                            RoundedCornerShape(18.dp)
                        ),

                    shape = RoundedCornerShape(18.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor = CardBg
                        )

                ) {

                    Column(

                        modifier =
                            Modifier.padding(18.dp)

                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(

                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        AccentBlue.copy(alpha = 0.12f)
                                    ),

                                contentAlignment =
                                    Alignment.Center

                            ) {

                                Icon(
                                    Icons.Filled.WaterDrop,
                                    contentDescription = null,
                                    tint = AccentBlue
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Column {

                                Text(
                                    text = "Log Water Arrival",

                                    style =
                                        MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SoftWhite
                                        )
                                )

                                Text(
                                    text = "Submit village water updates",

                                    style =
                                        MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Gray
                                        )
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )

                        OutlinedTextField(

                            value = village,

                            onValueChange = {
                                village = it
                            },

                            label = {
                                Text("Village Name")
                            },

                            leadingIcon = {

                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = AccentBlue
                                )
                            },

                            trailingIcon = {

                                if (village.isNotEmpty()) {

                                    IconButton(
                                        onClick = {
                                            village = ""
                                        }
                                    ) {

                                        Icon(
                                            Icons.Filled.Clear,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            },

                            singleLine = true,

                            keyboardOptions =
                                KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),

                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        submit()
                                    }
                                ),

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                OutlinedTextFieldDefaults.colors(

                                    focusedBorderColor =
                                        AccentBlue,

                                    unfocusedBorderColor =
                                        BorderDark,

                                    focusedTextColor =
                                        SoftWhite,

                                    unfocusedTextColor =
                                        SoftWhite,

                                    focusedContainerColor =
                                        DarkBg,

                                    unfocusedContainerColor =
                                        DarkBg,

                                    cursorColor =
                                        AccentBlue,

                                    focusedLabelColor =
                                        AccentBlue,

                                    unfocusedLabelColor =
                                        Color.Gray
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Button(

                            onClick = ::submit,

                            enabled =
                                village.isNotBlank(),

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = AccentBlue
                                )

                        ) {

                            Icon(
                                Icons.Filled.Send,
                                contentDescription = null,
                                tint = Color.Black
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text = "Submit Water Status",

                                fontWeight =
                                    FontWeight.Bold,

                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // 📋 Feed Header
            item {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Filled.Feed,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        Text(
                            text = "Recent Water Feed",

                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftWhite
                                )
                        )
                    }

                    if (waterStatusList.isNotEmpty()) {

                        Surface(

                            color =
                                AccentBlue.copy(alpha = 0.12f),

                            shape =
                                RoundedCornerShape(10.dp)

                        ) {

                            Text(

                                text =
                                    "${waterStatusList.size} entries",

                                style =
                                    MaterialTheme.typography.labelMedium.copy(
                                        color = AccentBlue,
                                        fontWeight = FontWeight.Bold
                                    ),

                                modifier =
                                    Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    )
                            )
                        }
                    }
                }
            }

            // 📭 Empty State
            if (waterStatusList.isEmpty()) {

                item {

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                BorderDark,
                                RoundedCornerShape(16.dp)
                            ),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor = CardBg
                            )

                    ) {

                        Column(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally

                        ) {

                            Icon(
                                Icons.Filled.WaterDrop,
                                contentDescription = null,
                                tint = AccentBlue.copy(alpha = 0.4f),
                                modifier = Modifier.size(44.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Text(
                                text = "No water updates yet",

                                style =
                                    MaterialTheme.typography.bodyLarge.copy(
                                        color = SoftWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text = "Submit a village update above",

                                style =
                                    MaterialTheme.typography.bodySmall.copy(
                                        color = Color.Gray
                                    )
                            )
                        }
                    }
                }

            } else {

                // 📋 Feed Items
                items(waterStatusList) { status ->

                    WaterStatusCard(status)
                }
            }

            item {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )
            }
        }
    }
}

@Composable
private fun WaterStatusCard(
    status: WaterStatus
) {

    val formattedTime =
        remember(status.timestamp) {

            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            ).format(
                Date(status.timestamp)
            )
        }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                BorderDark,
                RoundedCornerShape(16.dp)
            ),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = CardBg
            )

    ) {

        Row(

            modifier =
                Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.spacedBy(14.dp)

        ) {

            Box(

                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        AccentBlue.copy(alpha = 0.12f)
                    ),

                contentAlignment =
                    Alignment.Center

            ) {

                Icon(
                    Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(

                    text =
                        "Water reached ${status.village}",

                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftWhite
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(
                        text = formattedTime,

                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray
                            )
                    )
                }
            }

            Surface(

                color =
                    AccentGreen.copy(alpha = 0.12f),

                shape =
                    RoundedCornerShape(10.dp)

            ) {

                Text(

                    text = "Confirmed",

                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        ),

                    modifier =
                        Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                )
            }
        }
    }
}