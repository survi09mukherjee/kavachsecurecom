package com.kavachsecurecomm

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.kavachsecurecomm.data.AppDatabase
import com.kavachsecurecomm.security.CryptoUtils
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.kavachsecurecomm.ui.theme.KavachSecureCommTheme
import com.kavachsecurecomm.security.OpSecManager
import com.kavachsecurecomm.ui.onboarding.OnboardingScreen
import com.kavachsecurecomm.ui.chat.ChatScreen
import com.kavachsecurecomm.ui.dashboard.DashboardScreen
import com.kavachsecurecomm.ui.dashboard.EmergencyBroadcastScreen
import com.kavachsecurecomm.network.WebSocketManager
import com.kavachsecurecomm.data.MessageEntity
import com.kavachsecurecomm.ui.dashboard.DarkCharcoal
import java.util.UUID

class MainActivity : FragmentActivity() {
    private lateinit var opSecManager: OpSecManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Operational Security protocols (e.g., FLAG_SECURE)
        opSecManager = OpSecManager(this)
        opSecManager.applySecurityFlags()
        lifecycle.addObserver(opSecManager)

        enableEdgeToEdge()
        val webSocketManager = WebSocketManager()
        
        setContent {
            KavachSecureCommTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    
                    // Simple Navigation Router
                    var currentScreen by remember { mutableStateOf("registration") }
                    var currentUserId by remember { mutableStateOf("") }
                    var serverIp by remember { mutableStateOf("10.0.2.2") } // Default to emulator local
                    var activeChatContact by remember { mutableStateOf("Officer Command") }
                    
                    // Initialize Encryption Key (Demo fixed byte array)
                    val dbKey = "KavachSecureStoreKey123".toByteArray().copyOf(32)
                    val database = remember { 
                        try {
                            AppDatabase.getDatabase(this@MainActivity, dbKey)
                        } catch (e: Exception) {
                            null // Handle failure gracefully
                        }
                    }
                    val messageDao = database?.messageDao()
                    val scope = rememberCoroutineScope()

                    // Real-time Database Stream for UI
                    val chatMessages by (messageDao?.getChatHistoryAsFlow(currentUserId) ?: kotlinx.coroutines.flow.flowOf(emptyList())).collectAsState(initial = emptyList())
                    
                    // WebSocket Message Listener - Updates whenever the active chat contact changes
                    LaunchedEffect(activeChatContact) {
                        webSocketManager.onMessageReceived = { encryptedPayload ->
                            scope.launch {
                                try {
                                    val decryptedText = CryptoUtils.decrypt(encryptedPayload)
                                    val incomingMsg = MessageEntity(
                                        id = UUID.randomUUID().toString(),
                                        senderId = activeChatContact, 
                                        receiverId = currentUserId,
                                        groupId = null,
                                        plaintextPayload = decryptedText,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    messageDao?.insertMessage(incomingMsg)
                                } catch (e: Exception) {
                                    // Ignore non-decryptable system messages (e.g. "Welcome")
                                }
                            }
                        }
                    }

                    // Deep Military Theme Scaffold
                    Box(modifier = Modifier.fillMaxSize().background(DarkCharcoal).padding(innerPadding)) {
                        when (currentScreen) {
                            "splash" -> {
                                com.kavachsecurecomm.ui.onboarding.SplashScreen(
                                    onSplashComplete = {
                                        currentScreen = "onboarding"
                                    }
                                )
                            }
                            "onboarding" -> {
                                OnboardingScreen(
                                    onNavigateToHome = { authRole, serviceId, ip ->
                                        globalUserRole = authRole
                                        currentUserId = serviceId
                                        serverIp = ip
                                        currentScreen = "dashboard"
                                        // Connect to relay upon successful auth
                                        webSocketManager.connect("ws://$serverIp:3000", "dummy-jwt", serviceId) 
                                    },
                                    onNavigateToRegistration = {
                                        currentScreen = "registration"
                                    }
                                )
                            }
                            "registration" -> {
                                com.kavachsecurecomm.ui.onboarding.RegistrationScreen(
                                    onRegistrationSuccess = { authRole, serviceId ->
                                        globalUserRole = authRole
                                        currentUserId = serviceId
                                        currentScreen = "onboarding" // Move to onboarding (login) after registration
                                    },
                                    onNavigateBack = {
                                        currentScreen = "onboarding"
                                    }
                                )
                            }
                            "dashboard" -> {
                                DashboardScreen(
                                    lockedRole = globalUserRole,
                                    onNavigateToChat = { contact ->
                                        activeChatContact = contact
                                        currentScreen = "chat"
                                    },
                                    onNavigateToEmergency = {
                                        currentScreen = "emergency"
                                    },
                                    onLogout = {
                                        webSocketManager.disconnect()
                                        globalUserRole = ""
                                        currentScreen = "onboarding"
                                    }
                                )
                            }
                            "emergency" -> {
                                // Mocks connection simply for visual test
                                EmergencyBroadcastScreen(
                                    userRole = "Officer",
                                    webSocketManager = webSocketManager
                                )
                            }
                            "chat" -> {
                                ChatScreen(
                                    currentUserId = currentUserId,
                                    contactName = activeChatContact,
                                    userRole = globalUserRole,
                                    messages = chatMessages.filter { it.receiverId == activeChatContact || it.senderId == activeChatContact }, // Strictly isolate history per contact
                                    onSendMessage = { text ->
                                        scope.launch {
                                            // 1. Create locally & Save to encrypted DB
                                            val newMessage = MessageEntity(
                                                id = java.util.UUID.randomUUID().toString(),
                                                senderId = currentUserId,
                                                receiverId = activeChatContact,
                                                groupId = null,
                                                plaintextPayload = text,
                                                timestamp = System.currentTimeMillis()
                                            )
                                            messageDao?.insertMessage(newMessage)

                                            // 2. Encrypt & Relay to server
                                            try {
                                                val encrypted = CryptoUtils.encrypt(text)
                                                // Wrap in JSON for the new raw WebSocket backend
                                                val payload = org.json.JSONObject().apply {
                                                    put("type", "send_message")
                                                    put("senderId", currentUserId)
                                                    put("receiverId", activeChatContact)
                                                    put("encryptedPayload", encrypted)
                                                }.toString()
                                                webSocketManager.sendEncryptedPayload(payload)
                                            } catch (e: Exception) {
                                                // Fallback if encryption fails
                                                val payload = org.json.JSONObject().apply {
                                                    put("type", "send_message")
                                                    put("senderId", currentUserId)
                                                    put("receiverId", activeChatContact)
                                                    put("encryptedPayload", text)
                                                }.toString()
                                                webSocketManager.sendEncryptedPayload(payload)
                                            }
                                        }
                                    },
                                    onNavigateBack = {
                                        currentScreen = "dashboard"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}