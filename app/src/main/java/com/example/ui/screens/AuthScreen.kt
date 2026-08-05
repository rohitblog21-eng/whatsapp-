package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthUiState
import com.example.ui.theme.CipherGreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SecurityGold

@Composable
fun AuthScreen(
    authUiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onSignup: (String, String, String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onVerify2FA: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Login, 1: Signup
    var email by remember { mutableStateOf("commander@whisper.sec") }
    var username by remember { mutableStateOf("SecurityCommander") }
    var password by remember { mutableStateOf("Pass123456!") }
    var otpCode by remember { mutableStateOf("123456") }
    var twoFactorCode by remember { mutableStateOf("654321") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Header & Shield Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(EmeraldPrimary, shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security Shield",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Whisper AI Secure Chat",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Enterprise-Grade OWASP Top 10 • Argon2 KDF",
                fontSize = 12.sp,
                color = CipherGreen,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when {
                        // STEP 3: 2FA Prompt
                        authUiState.is2FARequired -> {
                            Text(
                                text = "Two-Factor Authentication (2FA)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enter the 6-digit authenticator code (Use '654321' for demo):",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = twoFactorCode,
                                onValueChange = { twoFactorCode = it },
                                label = { Text("2FA Code") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("2fa_input_field")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onVerify2FA(twoFactorCode) },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_2fa_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Authenticate Session", fontWeight = FontWeight.Bold)
                            }
                        }

                        // STEP 2: Email OTP Prompt
                        authUiState.isOtpRequired -> {
                            Text(
                                text = "Email OTP Verification",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "We sent a 6-digit verification code to ${authUiState.email} (Use '123456'):",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it },
                                label = { Text("6-Digit OTP Code") },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("otp_input_field")
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onVerifyOtp(otpCode) },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Verify Email OTP", fontWeight = FontWeight.Bold)
                            }
                        }

                        // STEP 1: Login / Signup Tab Form
                        else -> {
                            TabRow(
                                selectedTabIndex = selectedTab,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Tab(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    text = { Text("Email Login", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("login_tab")
                                )
                                Tab(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    text = { Text("Register", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("signup_tab")
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (selectedTab == 1) {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("Username") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("username_input")
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth().testTag("email_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password (Argon2 Hashed)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("password_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🔒 Argon2id KDF salt hash applied before dispatch",
                                fontSize = 11.sp,
                                color = CipherGreen
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (authUiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                Button(
                                    onClick = {
                                        if (selectedTab == 0) onLogin(email, password)
                                        else onSignup(email, username, password)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("auth_submit_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    Text(
                                        text = if (selectedTab == 0) "Sign In Securely" else "Create Encrypted Account",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (authUiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = authUiState.error,
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
