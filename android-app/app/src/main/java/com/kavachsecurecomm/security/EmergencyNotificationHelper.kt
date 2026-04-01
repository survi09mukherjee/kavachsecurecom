package com.kavachsecurecomm.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri

class EmergencyNotificationHelper(private val context: Context) {

    companion object {
        const val EMERGENCY_CHANNEL_ID = "kavach_emergency_override"
    }

    /**
     * Creates a high-priority notification channel that bypasses Android's 
     * Do Not Disturb (DnD) settings. Used exclusively for Officer broadcasts.
     */
    fun createEmergencyChannel() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // The channel name and description are visible to the user in Android Settings
        val name = "Emergency Broadcasts"
        val descriptionText = "Critical unit-wide alerts. Overrides Do Not Disturb."
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(EMERGENCY_CHANNEL_ID, name, importance).apply {
            description = descriptionText
            
            // Bypass Do Not Disturb
            setBypassDnd(true)
            
            // Optional: Set a custom loud piercing alarm sound instead of default ping
            val soundUri = Uri.parse("android.resource://" + context.packageName + "/raw/emergency_siren")
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
                
            // setSound(soundUri, audioAttributes)
            
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
        }

        notificationManager.createNotificationChannel(channel)
    }
}
