package com.kavachsecurecomm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kavachsecurecomm.ui.theme.KavachSecureCommTheme
import com.kavachsecurecomm.security.OpSecManager
import com.kavachsecurecomm.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    private lateinit var opSecManager: OpSecManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Operational Security protocols (e.g., FLAG_SECURE)
        opSecManager = OpSecManager(this)
        opSecManager.applySecurityFlags()
        lifecycle.addObserver(opSecManager)

        enableEdgeToEdge()
        setContent {
            KavachSecureCommTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Launch into Onboarding Flow natively
                    Box(modifier = Modifier.padding(innerPadding)) {
                        OnboardingScreen(
                            onNavigateToHome = {
                                // Transition to EmergencyBroadcastScreen/Chat later
                            }
                        )
                    }
                }
            }
        }
    }
}