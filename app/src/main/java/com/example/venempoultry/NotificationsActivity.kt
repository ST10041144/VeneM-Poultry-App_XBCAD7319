package com.example.venempoultry

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationsActivity : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Log the message for debugging purposes
        Log.d("NotificationsActivity", "Message received: ${remoteMessage.data}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            // Log the notification body for debugging
            Log.d("NotificationsActivity", "Notification body: ${it.body}")
            sendNotification(it.body ?: "Checkup due for batch")
        }
    }

    private fun sendNotification(messageBody: String) {
        val channelId = "default_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if on Android 8.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create and show the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Checkup Due")
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_notification)  // Ensure this icon exists
            .setAutoCancel(true)  // Auto cancel the notification when tapped
            .build()

        // Use a unique notification ID to avoid overwriting previous notifications
        val notificationId = System.currentTimeMillis().toInt()  // Unique ID for each notification
        notificationManager.notify(notificationId, notification)
    }
}
