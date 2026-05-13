package com.example.nammanala.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammanala.viewmodel.AuthViewModel

// Theme
private val DarkBg = Color(0xFF07111A)
private val CardBg = Color(0xFF101B2D)
private val CardBg2 = Color(0xFF0D2137)
private val AccentBlue = Color(0xFF00B4D8)
private val AccentCyan = Color(0xFF00D4F0)
private val AccentGreen = Color(0xFF2EC4B6)
private val SoftWhite = Color(0xFFEAF4FF)
private val FieldBg = Color(0xFF0A1828)
private val BorderColor = Color(0xFF1A3A50)
private val SubText = Color(0xFF7A9BB5)

@Composable
fun LoginScreen(
    authVm: AuthViewModel,
    onOpenSignup: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var btnPressed by remember { mutableStateOf(false) }

    val btnScale by animateFloatAsState(
        targetValue = if (btnPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btnScale"
    )

    // Subtle glow pulse on the logo
    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        // Subtle background radial glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentBlue.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF003554), Color(0xFF005F8E), AccentBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Water droplet shape using text emoji
                Text("💧", fontSize = 36.sp)
                // Glow overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AccentCyan.copy(alpha = glowAlpha * 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main card with gradient border effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF0A1E32),
                                Color(0xFF0D2A40),
                                Color(0xFF061828)
                            )
                        )
                    )
            ) {
                // Cyan top border accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, AccentBlue, AccentCyan, AccentBlue, Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = "LOG IN",
                        color = SoftWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = "Smart Canal Monitoring System",
                        color = AccentBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
                    )

                    // Email field
                    FuturisticTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Username / Email",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password field
                    FuturisticTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Login Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(btnScale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF007EA7), AccentBlue, AccentCyan)
                                )
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        btnPressed = true
                                        tryAwaitRelease()
                                        btnPressed = false
                                    },
                                    onTap = {
                                        isLoading = true
                                        authVm.login(email, password, onSuccess = {
                                            isLoading = false
                                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        })
                                        authVm.errorMessage.value?.let {
                                            isLoading = false
                                            Toast.makeText(context, "Login Failed: $it", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Log In",
                                color = Color(0xFF001A26),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Forgot + Signup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { /* TODO: forgot password */ }) {
                            Text(
                                "Forgot Password?",
                                color = SubText,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account? ",
                            color = SubText,
                            fontSize = 13.sp
                        )
                        TextButton(
                            onClick = onOpenSignup,
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                "Sign Up",
                                color = AccentBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Error
                    authVm.errorMessage.value?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = Color(0xFFE63946),
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Firebase badge
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Powered by Firebase",
                            color = SubText.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FuturisticTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(label, color = SubText, fontSize = 14.sp)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordToggle?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = SubText
                    )
                }
            }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = BorderColor,
            focusedTextColor = SoftWhite,
            unfocusedTextColor = SoftWhite,
            cursorColor = AccentCyan
        )
    )
}