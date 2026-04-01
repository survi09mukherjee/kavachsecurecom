package com.kavachsecurecomm.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kavachsecurecomm.R
import com.kavachsecurecomm.viewmodel.AuthViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// Military Color Palette
val ArmyGreen = Color(0xFF2E3B32)
val OliveDrab = Color(0xFF4B5320)
val TanText = Color(0xFFD2B48C)
val DarkCharcoal = Color(0xFF121212)

@Composable
fun OnboardingScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToHome: (String, String, String) -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    var tokenInput by remember { mutableStateOf("") }
    var serverIp by remember { mutableStateOf("10.0.2.2") } // Default to emulator local
    var userRole by remember { mutableStateOf("Officer") }
    var familyBindId by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "KAVACH",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TanText,
            letterSpacing = 4.sp
        )
        Text(
            text = "SECURE COMMAND HUB",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        // Role Selection
        Text(text = "CLASSIFICATION", color = TanText, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Officer", "Soldier", "Family").forEach { role ->
                FilterChip(
                    selected = userRole == role,
                    onClick = { userRole = role },
                    label = { Text(role, color = if(userRole == role) DarkCharcoal else TanText) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TanText,
                        containerColor = DarkCharcoal
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCharcoal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("NETWORK AUTHENTICATION", color = TanText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = serverIp,
                    onValueChange = { serverIp = it },
                    label = { Text("Server IPv4 Address", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = TanText,
                        unfocusedBorderColor = Color.DarkGray
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text("Enter Military Invite Token", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = TanText,
                        unfocusedBorderColor = Color.DarkGray
                    )
                )

                if (userRole == "Family") {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = familyBindId,
                        onValueChange = { familyBindId = it },
                        label = { Text("Soldier Service ID", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedBorderColor = TanText,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                authViewModel.validateAndRegister(tokenInput) {
                    onNavigateToHome(userRole, tokenInput, serverIp)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OliveDrab, disabledContainerColor = Color.DarkGray),
            enabled = authState !is AuthViewModel.AuthState.Loading && tokenInput.isNotBlank() && (userRole != "Family" || familyBindId.isNotBlank())
        ) {
            if (authState is AuthViewModel.AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TanText)
            } else {
                Text("AUTHENTICATE DEVICE", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegistration) {
            Text(
                text = "UNREGISTERED DEVICE? INITIATE NEW CLEARANCE.",
                color = TanText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (authState) {
            is AuthViewModel.AuthState.Error -> {
                Text(
                    text = (authState as AuthViewModel.AuthState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            is AuthViewModel.AuthState.Success -> {
                Text(
                    text = "CLEARANCE GRANTED.",
                    color = TanText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            else -> {}
        }
        }
    }
}

