package com.kavachsecurecomm.security

import android.app.Activity
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Enforces Operational Security (OpSec) protocols on the Android app.
 * E.g., Preventing screenshots, screen recordings, and hiding recent tasks previews.
 */
class OpSecManager(private val activity: Activity) : LifecycleEventObserver {

    /**
     * Call this inside MainActivity's onCreate ONE time.
     */
    fun applySecurityFlags() {
        // Prevent Screenshots & Screen Recording
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                // E.g., we could blank out the screen or drop encryption keys from memory
            }
            Lifecycle.Event.ON_RESUME -> {
                // Re-prompt for Biometrics if app was in background for too long
            }
            else -> {}
        }
    }
}
