package com.kavachsecurecomm.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.paint
import androidx.compose.material.icons.filled.ArrowBack
import com.kavachsecurecomm.R
import com.kavachsecurecomm.data.MessageEntity
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    contactName: String,
    userRole: String,
    messages: List<MessageEntity>, // From Room DB Flow
    onSendMessage: (String) -> Unit, // Wired to DoubleRatchetEngine + WebSocket
    onNavigateBack: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val isFamilyChat = userRole == "Family" || contactName.contains("Family", ignoreCase = true)
    val bgImageId = if (isFamilyChat) R.drawable.aesthetic_bg else R.drawable.camo_bg

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("KavachPrefs", Context.MODE_PRIVATE)
    var familyBgUri by remember { mutableStateOf(sharedPrefs.getString("family_bg_uri", null)) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.filesDir, "family_bg.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                
                val savedUri = Uri.fromFile(file).toString()
                sharedPrefs.edit().putString("family_bg_uri", savedUri).apply()
                familyBgUri = savedUri
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isFamilyChat && familyBgUri != null) {
            AsyncImage(
                model = familyBgUri,
                contentDescription = "Family Custom Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = bgImageId),
                contentDescription = "Default Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // HUD Readable filter
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

        Column(modifier = Modifier.fillMaxSize()) {
        // App Bar
        TopAppBar(
            title = { Text("Secure Chat: $contactName", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            actions = {
                if (isFamilyChat) {
                    // Upload Custom Background Button
                    IconButton(onClick = { launcher.launch("image/*") }) {
                        Icon(Icons.Default.Add, contentDescription = "Upload Background", tint = Color.White)
                    }
                }
                
                // Judge Demo: Simulate Reply Button
                IconButton(onClick = { 
                    onSendMessage("SIMULATED_REPLY_ACTION_TRIGGERED") 
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Simulate Reply",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f))
        )

        // Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            reverseLayout = true // Chat apps usually start from bottom
        ) {
            // Note: Since reverseLayout=true, list should be passed in descending order or reversed
            items(messages.reversed()) { msg ->
                MessageBubble(message = msg, isMine = msg.senderId == currentUserId)
            }
        }

        // Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type an encrypted message...") },
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = {
                    if (textState.isNotBlank()) {
                        coroutineScope.launch {
                            // The ViewModel will take this, encrypt it natively, store in DB, and WebSocket Emit
                            onSendMessage(textState)
                            textState = ""
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
}


@Composable
fun MessageBubble(message: MessageEntity, isMine: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isMine) MaterialTheme.colorScheme.primary.copy(alpha=0.95f) else Color.DarkGray.copy(alpha=0.95f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMine) 16.dp else 0.dp,
                        bottomEnd = if (isMine) 0.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.plaintextPayload,
                color = Color.White
            )
        }
    }
}
