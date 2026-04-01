package com.kavachsecurecomm.ui.dashboard

import android.media.RingtoneManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kavachsecurecomm.network.WebSocketManager

val RadarGreen = Color(0xFF00FF00)
val RadarRed = Color(0xFFFF0000)

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

    val context = LocalContext.current
    var isBroadcasting by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var previousVolume by remember { mutableIntStateOf(-1) }

    // Siren Audio Logic mapped explicitly to Media Stream (Bluetooth/Earpiece/Speaker)
    LaunchedEffect(isBroadcasting) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        
        if (isBroadcasting) {
            try {
                // 1. Force the Media Volume to Absolute Maximum
                previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

                // 2. Prepare the Air Raid Siren
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(context, com.kavachsecurecomm.R.raw.siren).apply {
                        val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA) // Forces playback over Bluetooth Media channels
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                        setAudioAttributes(attributes)
                        isLooping = true
                    }
                }
                mediaPlayer?.start()
                showWarningDialog = true
                Toast.makeText(context, "Transmitting Emergency Protocol to all Soldiers...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mediaPlayer?.pause()
            mediaPlayer?.seekTo(0)
            showWarningDialog = false
            
            // 3. Restore the user's previous volume when All Clear is issued
            if (previousVolume != -1) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { 
            mediaPlayer?.stop()
            mediaPlayer?.release() 
        }
    }

    // Radar Animation Sweep
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isBroadcasting) "DEFCON 1: ACTIVE THREAT" else "DEFCON 5: SAFE MODE",
            fontWeight = FontWeight.ExtraBold,
            color = if (isBroadcasting) RadarRed else RadarGreen,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Radar UI
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.width / 2
                val center = Offset(x = radius, y = radius)
                val radarColor = if (isBroadcasting) RadarRed else RadarGreen

                // Grid circles
                for (i in 1..4) {
                    drawCircle(color = radarColor, radius = radius * (i / 4f), center = center, style = Stroke(width = 2f))
                }
                
                // Crosshairs
                drawLine(color = radarColor, start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 2f)
                drawLine(color = radarColor, start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 2f)

                // Sweep cone
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color.Transparent, radarColor.copy(alpha = 0.6f)),
                        center = center
                    ),
                    startAngle = sweepAngle - 90f,
                    sweepAngle = 90f,
                    useCenter = true,
                    style = Fill,
                    topLeft = Offset.Zero,
                    size = size
                )

                // Enemy dots
                if (isBroadcasting) {
                    drawCircle(Color.White, radius = 8f, center = Offset(radius * 0.6f, radius * 0.5f))
                    drawCircle(Color.White, radius = 8f, center = Offset(radius * 1.3f, radius * 1.4f))
                    drawCircle(Color.White, radius = 8f, center = Offset(radius * 1.6f, radius * 0.7f))
                    drawCircle(Color.White, radius = 8f, center = Offset(radius * 0.8f, radius * 1.6f))
                    drawCircle(Color.White, radius = 8f, center = Offset(radius * 1.1f, radius * 0.3f))
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Action Buttons
        if (!isBroadcasting) {
            Button(
                onClick = {
                    isBroadcasting = true
                    val broadcastPayload = """
                        {
                            "event": "emergency_broadcast",
                            "senderId": "officer_123",
                            "targetUnitId": "GLOBAL",
                            "encryptedPayload": "mocked_ciphertext_here"
                        }
                    """.trimIndent()
                    webSocketManager.sendEncryptedPayload(broadcastPayload)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(65.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("ISSUE EMERGENCY BROADCAST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else {
            Button(
                onClick = {
                    isBroadcasting = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = RadarGreen.copy(alpha = 0.8f)),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(65.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DarkCharcoal)
                Spacer(modifier = Modifier.width(12.dp))
                Text("ISSUE ALL CLEAR", color = DarkCharcoal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    // Modal Warning Pops up exactly when Broadcast hits
    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss without All Clear or Confirm */ },
            containerColor = Color(0xFF2B0000),
            titleContentColor = RadarRed,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = RadarRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CRITICAL ALERT")
                }
            },
            text = {
                Text("WARNING: ENEMY DETECTED.\n\nOverride signals have been sent to all Solders. All units must fast deploy for defense immediately.", fontSize = 16.sp)
            },
            confirmButton = {
                Button(
                    onClick = { showWarningDialog = false }, // Just closes dialog to see radar, siren continues
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("ACKNOWLEDGE", color = Color.White)
                }
            }
        )
    }
}
