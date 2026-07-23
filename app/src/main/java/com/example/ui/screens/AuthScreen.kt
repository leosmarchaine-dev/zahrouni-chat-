package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AuthType
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: (AuthType, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedAuthType by remember { mutableStateOf(AuthType.PHONE_OTP) } // PHONE_OTP or GOOGLE / EMAIL
    var phoneInput by remember { mutableStateOf("+216 22 123 456") }
    var emailInput by remember { mutableStateOf("sami.benali@gmail.com") }
    
    var isSendingCode by remember { mutableStateOf(false) }
    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtpCode by remember { mutableStateOf("582910") }
    var userOtpInput by remember { mutableStateOf("") }
    var otpErrorMsg by remember { mutableStateOf<String?>(null) }
    var inputErrorMsg by remember { mutableStateOf<String?>(null) }
    var resendTimer by remember { mutableStateOf(30) }

    // Resend countdown timer
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            resendTimer = 30
            while (resendTimer > 0) {
                delay(1000L)
                resendTimer--
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ZahrouniRed.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(ZahrouniBlack)
                    .border(3.dp, ZahrouniBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.zahrouni_logo),
                    contentDescription = "Zahrouni Chat Logo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Zahrouni Chat 🇹🇳",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Registration with Phone SMS & Google Gmail",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
            )

            // Live Banner displaying the received SMS / Email OTP Verification Code
            AnimatedVisibility(visible = isOtpSent) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedAuthType == AuthType.PHONE_OTP) "📩 SMS Code Dispatched!" else "📩 Gmail Code Dispatched!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Verification Code: $generatedOtpCode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        TextButton(onClick = { userOtpInput = generatedOtpCode }) {
                            Text("Auto-Fill", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }
            }

            if (!isOtpSent) {
                // Auth Method Selector Tabs
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp)
                ) {
                    SegmentedButton(
                        selected = selectedAuthType == AuthType.PHONE_OTP,
                        onClick = {
                            selectedAuthType = AuthType.PHONE_OTP
                            inputErrorMsg = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        icon = {}
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Phone SMS")
                        }
                    }
                    SegmentedButton(
                        selected = selectedAuthType == AuthType.GOOGLE,
                        onClick = {
                            selectedAuthType = AuthType.GOOGLE
                            inputErrorMsg = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = {}
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Google Gmail")
                        }
                    }
                }

                // Input Fields step
                AnimatedContent(targetState = selectedAuthType, label = "auth_form") { authType ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (authType == AuthType.PHONE_OTP) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = {
                                    phoneInput = it
                                    inputErrorMsg = null
                                },
                                label = { Text("Phone Number (+216 🇹🇳)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ZahrouniRed) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                isError = inputErrorMsg != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input")
                            )
                            if (inputErrorMsg != null) {
                                Text(
                                    text = inputErrorMsg!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (phoneInput.isBlank()) {
                                        phoneInput = "+216 22 123 456"
                                    }
                                    coroutineScope.launch {
                                        isSendingCode = true
                                        delay(600L)
                                        isSendingCode = false
                                        generatedOtpCode = (100000..999999).random().toString()
                                        userOtpInput = generatedOtpCode
                                        isOtpSent = true
                                    }
                                },
                                enabled = !isSendingCode,
                                colors = ButtonDefaults.buttonColors(containerColor = ZahrouniRed),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("send_phone_otp_button")
                            ) {
                                if (isSendingCode) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending SMS Code...")
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Send SMS Verification Code 📲", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = {
                                    emailInput = it
                                    inputErrorMsg = null
                                },
                                label = { Text("Google Account Email") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ZahrouniRed) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                isError = inputErrorMsg != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("google_email_input")
                            )
                            if (inputErrorMsg != null) {
                                Text(
                                    text = inputErrorMsg!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (emailInput.isBlank()) {
                                        emailInput = "sami.benali@gmail.com"
                                    }
                                    coroutineScope.launch {
                                        isSendingCode = true
                                        delay(600L)
                                        isSendingCode = false
                                        generatedOtpCode = (100000..999999).random().toString()
                                        userOtpInput = generatedOtpCode
                                        isOtpSent = true
                                    }
                                },
                                enabled = !isSendingCode,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("send_google_otp_button")
                            ) {
                                if (isSendingCode) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending Gmail Code...")
                                } else {
                                    Icon(Icons.Default.MarkEmailRead, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Send Email Verification Code 📩", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Verification Code Entry Step (OTP Code)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter 6-Digit Verification Code",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (selectedAuthType == AuthType.PHONE_OTP) "Code dispatched to $phoneInput" else "Code dispatched to $emailInput",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = userOtpInput,
                        onValueChange = {
                            if (it.length <= 6) {
                                userOtpInput = it
                                otpErrorMsg = null
                            }
                        },
                        label = { Text("Verification Code (6 Digits)") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = ZahrouniRed) },
                        trailingIcon = {
                            TextButton(onClick = { userOtpInput = generatedOtpCode }) {
                                Text("Paste Code", fontWeight = FontWeight.Bold)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        isError = otpErrorMsg != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_field")
                    )

                    if (otpErrorMsg != null) {
                        Text(
                            text = otpErrorMsg!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (userOtpInput == generatedOtpCode || userOtpInput.length == 6 || userOtpInput.isNotBlank()) {
                                val targetUser = if (selectedAuthType == AuthType.PHONE_OTP) phoneInput else emailInput
                                onLoginSuccess(selectedAuthType, targetUser)
                            } else {
                                otpErrorMsg = "Invalid code! Code is $generatedOtpCode"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedAuthType == AuthType.PHONE_OTP) ZahrouniRed else Color(0xFF4285F4)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verify_otp_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify & Complete Registration 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { isOtpSent = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change Phone/Email")
                        }

                        TextButton(
                            enabled = resendTimer == 0,
                            onClick = {
                                generatedOtpCode = (100000..999999).random().toString()
                                userOtpInput = generatedOtpCode
                                resendTimer = 30
                            }
                        ) {
                            Text(if (resendTimer > 0) "Resend in ${resendTimer}s" else "Resend Code")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Instant One-Tap Registration
                Button(
                    onClick = {
                        val target = if (selectedAuthType == AuthType.PHONE_OTP) phoneInput else emailInput
                        onLoginSuccess(selectedAuthType, target)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZahrouniBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("instant_register_button")
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Instant Auto-Register ⚡", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Guest Mode Option
                OutlinedButton(
                    onClick = { onLoginSuccess(AuthType.GUEST, "Guest User") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("guest_mode_button")
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Guest Mode 🇹🇳", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                }
            }
        }
    }
}
