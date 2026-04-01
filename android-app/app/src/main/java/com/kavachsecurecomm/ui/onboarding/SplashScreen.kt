package com.kavachsecurecomm.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

val Saffron = Color(0xFFFF9933)
val IndiaWhite = Color(0xFFFFFFFF)
val IndiaGreen = Color(0xFF138808)
val NavyBlue = Color(0xFF000080)

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var stage by remember { mutableIntStateOf(1) }
    var visible by remember { mutableStateOf(false) }

    // Timing Logic
    LaunchedEffect(Unit) {
        visible = true
        delay(2500) // Show Stage 1 for 2.5s
        visible = false
        delay(500) // Fade transition
        stage = 2
        visible = true
        delay(3500) // Show Stage 2 for 3.5s
        visible = false
        delay(500)
        onSplashComplete()
    }

    val militaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF2E3B32), Color(0xFF4B5320))
    )
    val tricolourGradient = Brush.linearGradient(
        colors = listOf(Saffron, IndiaWhite, IndiaGreen)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (stage == 1) militaryGradient else tricolourGradient),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible && stage == 1,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text(
                    text = "kavachsecurecommunication",
                    color = Color(0xFFD2B48C),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        AnimatedVisibility(
            visible = visible && stage == 2,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Indian Flag Block
                IndianFlagBlock()

                Spacer(modifier = Modifier.height(48.dp))

                // Typography Stack
                Text(
                    text = "JAI HIND",
                    color = Saffron.copy(alpha = 0.9f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Vande Mataram with inline Chakra
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VANDE",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Rotating Chakra Animation
                    val infiniteTransition = rememberInfiniteTransition()
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(4000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                    
                    AshokaChakra(
                        modifier = Modifier.size(28.dp),
                        rotationAngle = rotation
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "MATARAM",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BHARAT MATA KI JAI",
                    color = IndiaGreen.copy(alpha = 0.9f),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Bottom Indian Flag Block
                IndianFlagBlock()
            }
        }
    }
}

@Composable
fun IndianFlagBlock() {
    Column(modifier = Modifier.width(200.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Saffron))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(IndiaWhite))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(IndiaGreen))
    }
}

@Composable
fun AshokaChakra(modifier: Modifier = Modifier, rotationAngle: Float = 0f) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        rotate(rotationAngle, center) {
            // Outer blue circle
            drawCircle(color = NavyBlue, radius = radius, center = center, style = Stroke(width = 4f))
            // Inner mini circle
            drawCircle(color = NavyBlue, radius = radius * 0.15f, center = center, style = Stroke(width = 2f))
            
            // 24 Spokes perfectly mapped via trigonometry
            for (i in 0 until 24) {
                val angle = i * (360.0 / 24.0) * (Math.PI / 180.0)
                val startX = center.x + (radius * 0.15f) * cos(angle).toFloat()
                val startY = center.y + (radius * 0.15f) * sin(angle).toFloat()
                val endX = center.x + radius * cos(angle).toFloat()
                val endY = center.y + radius * sin(angle).toFloat()
                
                drawLine(
                    color = NavyBlue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.5f
                )
            }
        }
    }
}
