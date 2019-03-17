package com.example.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class NotificationChannel
    (ctx: Context) : ContextWrapper(ctx) {
    private var manager: NotificationManager? = null


    private val smallIcon: Int
        get() = android.R.drawable.stat_notify_chat

    init {

        val chan1 = NotificationChannel(
            PRIMARY_CHANNEL,
            "channel1", NotificationManager.IMPORTANCE_DEFAULT
        )
        chan1.lightColor = Color.GREEN
        chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager()!!.createNotificationChannel(chan1)

        val chan2 = NotificationChannel(
            SECONDARY_CHANNEL,
            "channel2", NotificationManager.IMPORTANCE_HIGH
        )
        chan2.lightColor = Color.BLUE
        chan2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        getManager()?.createNotificationChannel(chan2)
    }

    fun getNotification1(title: String, body: String): Notification.Builder {
        return Notification.Builder(applicationContext, PRIMARY_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
    }

    fun getNotification2(title: String, body: String): Notification.Builder {
        return Notification.Builder(applicationContext, SECONDARY_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
    }

    fun notify(id: Int, notification: Notification.Builder) {
        getManager()!!.notify(id, notification.build())
    }

    private fun getManager(): NotificationManager? {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager
    }

    companion object {
        val PRIMARY_CHANNEL = "default"
        val SECONDARY_CHANNEL = "second"
    }
}
