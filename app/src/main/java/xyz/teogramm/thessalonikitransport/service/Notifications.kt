package xyz.teogramm.thessalonikitransport.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import xyz.teogramm.thessalonikitransport.R

/**
 * Static class containing fields and methods for managing the notifications of this application
 */
class Notifications {
    companion object{
        val ARRIVALS_CHANNEL_NAME = "arrivals"
        val PERSISTENT_CHANNEL_NAME = "persistent"

        fun createNotificationChannels(context: Context){
            createPersistentNotificationChannel(context)
            createArrivalNotificationsChannel(context)
        }

        private fun createPersistentNotificationChannel(context: Context){
            val displayName = context.getString(R.string.persistentNotificationChannelName)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(PERSISTENT_CHANNEL_NAME,displayName,importance)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        private fun createArrivalNotificationsChannel(context: Context){
            val displayName = context.getString(R.string.liveArrivalsNotificationChannelName)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(ARRIVALS_CHANNEL_NAME,displayName,importance)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}