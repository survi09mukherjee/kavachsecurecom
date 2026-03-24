package com.kavachsecurecomm.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kavachsecurecomm.network.WebSocketManager

@Composable
fun EmergencyBroadcastScreen(
    userRole: String, // E.g., "Officer"
    webSocketManager: WebSocketManager
) {
    if (userRole != "Officer") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Access Denied: Officer Privileges Required", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    var isBroadcasting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8B0000)) // Deep Red background for Emergency context
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Emergency",
            tint = Color.White,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "INITIATE EMERGENCY BROADCAST",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This will bypass 'Do Not Disturb' on all unit devices and sound a persistent alarm.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                // In Reality: Prompt Biometrics to confirm intent BEFORE sending
                isBroadcasting = true
                
                // Pack the simulated alert data 
                // Payload must be encrypted via DoubleRatchetEngine before sending
                val broadcastPayload = """
                    {
                        "event": "emergency_broadcast",
                        "senderId": "officer_123",
                        "targetUnitId": "unit_alpha",
                        "encryptedPayload": "mocked_ciphertext_here"
                    }
                """.trimIndent()

                webSocketManager.sendEncryptedPayload(broadcastPayload)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp)
        ) {
            Text(
                if (isBroadcasting) "SIGNAL TRANSMITTED" else "SWIPE TO BROADCAST (MOCK)",
                color = Color.Red,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
