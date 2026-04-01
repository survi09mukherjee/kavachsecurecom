package com.kavachsecurecomm.ui.onboarding

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.kavachsecurecomm.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kavachsecurecomm.data.MockDataset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun Context.findActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegistrationSuccess: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var userRole by remember { mutableStateOf("Soldier") }
    var fullName by remember { mutableStateOf("") }
    var serviceId by remember { mutableStateOf("") }
    var rankTitle by remember { mutableStateOf("") }
    var battalionPost by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var soldierIdLink by remember { mutableStateOf("") }
    var showErrorAlert by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.intro),
            contentDescription = "Intro Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "NEW COMPONENT CLEARANCE",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFD2B48C),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select Registration Role",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Officer", "Soldier", "Family").forEach { role ->
                    FilterChip(
                        selected = userRole == role,
                        onClick = { userRole = role },
                        label = { Text(role, color = if (userRole == role) Color(0xFF121212) else Color(0xFFD2B48C)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD2B48C),
                            containerColor = Color(0xFF121212)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color(0xFFD2B48C),
                            enabled = true,
                            selected = userRole == role
                        )
                    )
                }
            }

            if (showErrorAlert) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "RED ALERT: $errorMessage",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B32)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Legal Name", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedBorderColor = Color(0xFFD2B48C),
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (userRole == "Officer" || userRole == "Soldier") {
                        OutlinedTextField(
                            value = serviceId,
                            onValueChange = { serviceId = it },
                            label = { Text("Military Service ID No.", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray, focusedBorderColor = Color(0xFFD2B48C), unfocusedBorderColor = Color.DarkGray)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = rankTitle,
                            onValueChange = { rankTitle = it },
                            label = { Text("Rank / Title", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray, focusedBorderColor = Color(0xFFD2B48C), unfocusedBorderColor = Color.DarkGray)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = battalionPost,
                            onValueChange = { battalionPost = it },
                            label = { Text("Assigned Unit / Battalion Code", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray, focusedBorderColor = Color(0xFFD2B48C), unfocusedBorderColor = Color.DarkGray)
                        )
                    } else {
                        OutlinedTextField(
                            value = relation,
                            onValueChange = { relation = it },
                            label = { Text("Relation (e.g. Spouse, Parent)", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray, focusedBorderColor = Color(0xFFD2B48C), unfocusedBorderColor = Color.DarkGray)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = soldierIdLink,
                            onValueChange = { soldierIdLink = it },
                            label = { Text("Direct Link Soldier ID", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray, focusedBorderColor = Color(0xFFD2B48C), unfocusedBorderColor = Color.DarkGray)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val activity = context.findActivity()
                    if (activity == null) {
                        Toast.makeText(context, "MFA Error: FragmentActivity context mapping failed.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val targetId = if (userRole == "Family") soldierIdLink else serviceId
                    val foundUser = MockDataset.allUsers.find { it.serviceId == targetId }

                    if (foundUser == null) {
                        errorMessage = "UNAUTHORIZED SERVICE ID. DEVICE LOCKDOWN INITIATED."
                        showErrorAlert = true
                        Toast.makeText(context, "RED ALERT: UNAUTHORIZED ACCESS ATTEMPT", Toast.LENGTH_LONG).show()
                        return@Button
                    } else {
                        showErrorAlert = false
                    }

                    try {
                        val executor = ContextCompat.getMainExecutor(context)
                        val biometricPrompt = BiometricPrompt(
                            activity,
                            executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                    super.onAuthenticationError(errorCode, errString)
                                    Toast.makeText(context, "Hardware Error: $errString", Toast.LENGTH_SHORT).show()
                                }

                                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                    super.onAuthenticationSucceeded(result)
                                    Toast.makeText(context, "Physical Biological Signature Captured.", Toast.LENGTH_LONG).show()
                                    onRegistrationSuccess(userRole, targetId)
                                }

                                override fun onAuthenticationFailed() {
                                    super.onAuthenticationFailed()
                                    Toast.makeText(context, "Signature match failed.", Toast.LENGTH_SHORT).show()
                                }
                            })

                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Initialize 3FA Lock")
                            .setSubtitle("Bind physical identity metrics to Zero-Trust keys.")
                            .setNegativeButtonText("ABORT")
                            .build()

                        biometricPrompt.authenticate(promptInfo)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Hardware Interfacing Exception: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B5320)),
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("ENROLL BIOMETRICS & REGISTER", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text("← CANCEL & RETURN TO LOGIN", color = Color(0xFFD2B48C))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
