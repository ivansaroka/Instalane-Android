package com.infinix.instalane.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.infinix.instalane.R
import com.infinix.instalane.data.SingletonNotification
import com.infinix.instalane.ui.start.SplashActivity

class AppMessagingService : FirebaseMessagingService() {

    companion object {
        const val KEY_NOTIFICATION_DATA = "notificationData"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val bundle = Bundle().apply { remoteMessage.data.forEach { (k, v) -> putString(k, v) } }
        val intent = Intent(applicationContext, SplashActivity::class.java).putExtras(bundle)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val contentIntent = PendingIntent.getActivity(applicationContext, 0, intent, flags)

        val notification =
            NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), notification)
        Log.i("notification", remoteMessage.data.toString())

        SingletonNotification.instance.onNotificationReceived?.invoke()
    }

    override fun onNewToken(token: String) {
        Log.d("FIREBASEMESSAGE", "Refreshed token: " + token)
        super.onNewToken(token)
    }
}