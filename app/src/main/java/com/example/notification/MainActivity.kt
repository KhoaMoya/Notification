package com.example.notification

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Notification
import android.widget.TextView
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private val NOTI_PRIMARY1 = 1100
    private val NOTI_PRIMARY2 = 1101
    private val NOTI_SECONDARY1 = 1200
    private val NOTI_SECONDARY2 = 1201


    private var ui: MainUi? = null


    private var noti: NotificationChannel? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        noti = NotificationChannel(this)
        ui = MainUi(findViewById(R.id.main_activity))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(id: Int, title: String) {
        var nb: Notification.Builder? = null
        when (id) {
            NOTI_PRIMARY1 -> nb = noti?.getNotification1(title, "p1")

            NOTI_PRIMARY2 -> nb = noti?.getNotification1(title, "p2")

            NOTI_SECONDARY1 -> nb = noti?.getNotification2(title, "s1")

            NOTI_SECONDARY2 -> nb = noti?.getNotification2(title, "s2")
        }
        if (nb != null) {
            noti?.notify(id, nb)
        }
    }

    fun goToNotificationSettings() {
        val i = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        i.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(i)
    }

    fun goToNotificationSettings(channel: String) {
        val i = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
        i.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        i.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
        startActivity(i)
    }

    internal inner class MainUi (root: View) : View.OnClickListener {
        private val titlePrimary: TextView? = null
        private val titleSecondary: TextView? = null

        private val titlePrimaryText: String
            get() = if (titlePrimary != null) {
                titlePrimary.text.toString()
            } else ""

        private val titleSecondaryText: String
            get() {
                return if (titlePrimary != null) {
                    titleSecondary?.text.toString()
                } else ""
            }

        init {
            (root.findViewById(R.id.button) as Button).setOnClickListener(this)
            (root.findViewById(R.id.button2) as Button).setOnClickListener(this)
            (root.findViewById(R.id.button3) as Button).setOnClickListener(this)

            (root.findViewById(R.id.button4) as Button).setOnClickListener(this)
            (root.findViewById(R.id.button5) as Button).setOnClickListener(this)
            (root.findViewById(R.id.button6) as Button).setOnClickListener(this)

            (root.findViewById(R.id.button7) as Button).setOnClickListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onClick(view: View) {
            when (view.id) {
                R.id.button -> sendNotification(NOTI_PRIMARY1, titlePrimaryText)
                R.id.button2 -> sendNotification(NOTI_PRIMARY2, titlePrimaryText)
                R.id.button3 -> goToNotificationSettings(NotificationChannel.PRIMARY_CHANNEL)

                R.id.button4 -> sendNotification(NOTI_SECONDARY1, titleSecondaryText)
                R.id.button5 -> sendNotification(NOTI_SECONDARY2, titleSecondaryText)
                R.id.button6 -> goToNotificationSettings(NotificationChannel.SECONDARY_CHANNEL)
                R.id.button7 -> goToNotificationSettings()
                else -> Log.e("hihi", "Unknown click event.")
            }
        }

    }
}
