package com.kavachsecurecomm.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

val ArmyGreen = Color(0xFF2E3B32)
val OliveDrab = Color(0xFF4B5320)
val TanText = Color(0xFFD2B48C)
val DarkCharcoal = Color(0xFF121212)

@Composable
fun DashboardScreen(
    lockedRole: String, // Received permanently from Onboarding
    onNavigateToChat: (String) -> Unit,
    onNavigateToEmergency: () -> Unit,
    onLogout: () -> Unit
) {
    // Dynamic Contact List State - Clustered into Folders
    var officialContacts by remember {
        mutableStateOf(
            when (lockedRole) {
                "Officer" -> listOf("Alpha Platoon", "Base Command HQ")
                "Soldier" -> listOf("Platoon Commander")
                else -> emptyList() // Family has no required official contacts by default
            }
        )
    }

    var familyContacts by remember {
        mutableStateOf(
            when (lockedRole) {
                "Officer" -> listOf("Family Home")
                "Soldier" -> listOf("My Family")
                else -> listOf("My Soldier (ID: 402)")
            }
        )
    }

    // Add New Contact Dialog State
    var showAddDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactTargetFolder by remember { mutableStateOf("Official") }

    val bgImage = when (lockedRole) {
        "Officer" -> com.kavachsecurecomm.R.drawable.officials
        "Soldier" -> com.kavachsecurecomm.R.drawable.soldiers
        "Family" -> com.kavachsecurecomm.R.drawable.family
        else -> com.kavachsecurecomm.R.drawable.intro
    }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = bgImage),
            contentDescription = "Dashboard Background",
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(16.dp)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(48.dp)) // Equalizer for center text
            Text(
                text = "KAVACH HUD",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TanText,
                letterSpacing = 2.sp
            )
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Logout",
                    tint = TanText
                )
            }
        }

        Badge(
            containerColor = OliveDrab,
            contentColor = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(" CLEARANCE LEVEL: ${lockedRole.uppercase()} ", modifier = Modifier.padding(4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = ArmyGreen)
        Spacer(modifier = Modifier.height(16.dp))



        // Active Connections List Box
        Card(
            colors = CardDefaults.cardColors(containerColor = ArmyGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Takes up remaining screen space nicely
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    // FOLDER 0: EMERGENCY (Officers Only)
                    if (lockedRole == "Officer") {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("EMERGENCY PROTOCOLS", color = Color(0xFFFF4444), fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = Color(0xFF8B0000))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item {
                            Button(
                                onClick = onNavigateToEmergency,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ISSUE EMERGENCY BROADCAST", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }

                    // FOLDER 1: OFFICIAL WORK
                    if (lockedRole != "Family") { // Families don't generally add official chains
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("OFFICIAL DIRECTORY", color = TanText, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { 
                                    newContactTargetFolder = "Official"
                                    showAddDialog = true 
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Official", tint = TanText)
                                }
                            }
                            HorizontalDivider(color = OliveDrab)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(officialContacts) { contact ->
                            Button(
                                onClick = { onNavigateToChat(contact) },
                                colors = ButtonDefaults.buttonColors(containerColor = OliveDrab),
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Encrypted Link: $contact",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }

                    // FOLDER 2: FAMILY CHANNELS
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PERSONAL CHANNELS", color = TanText, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { 
                                newContactTargetFolder = "Family"
                                showAddDialog = true 
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Family", tint = TanText)
                            }
                        }
                        HorizontalDivider(color = OliveDrab)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(familyContacts) { contact ->
                        Button(
                            onClick = { onNavigateToChat("Family - $contact") }, // Passes the "Family" keyword to trigger Aesthetic Context
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCharcoal),
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = TanText)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Secure Link: $contact",
                                color = TanText,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            }
        }
    }

    // Add Contact Dialog Pop-up
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = DarkCharcoal,
            titleContentColor = TanText,
            textContentColor = Color.White,
            title = { Text("Establish New $newContactTargetFolder Link") },
            text = {
                OutlinedTextField(
                    value = newContactName,
                    onValueChange = { newContactName = it },
                    label = { Text("Target Unit/Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.LightGray,
                        focusedBorderColor = TanText,
                        unfocusedBorderColor = ArmyGreen
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newContactName.isNotBlank()) {
                            if (newContactTargetFolder == "Official") {
                                officialContacts = officialContacts + newContactName
                            } else {
                                familyContacts = familyContacts + newContactName
                            }
                            newContactName = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OliveDrab)
                ) {
                    Text("SECURE SAVE", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL", color = Color.Gray)
                }
            }
        )
    }
}
