package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.RemoteInput
import android.support.v7.app.AppCompatActivity
import android.util.Log


class NotiActivity(internal var myFrag: NotiFragment) : AppCompatActivity() {
    internal var TAG = "NotiActivity"

    private val mDeleteReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            myFrag.updateNumberOfNotifications()
        }
    }

    private val mReadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceiveRead")
            val conversationId = intent.getIntExtra(CONVERSATION_ID, -1)
            if (conversationId != -1) {
                myFrag.NotificationRead(conversationId)
            }
        }
    }

    private val mReplyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceiveReply")
            val conversationId = intent.getIntExtra(CONVERSATION_ID, -1)
            if (conversationId != -1) {
                val remoteInput = RemoteInput.getResultsFromIntent(intent)
                if (remoteInput != null) {
                    val replyMessage = remoteInput!!.getCharSequence(EXTRA_REMOTE_REPLY)!!.toString()
                    Log.d(TAG, "Notification $conversationId reply is $replyMessage")
                    val notificationManager = NotificationManagerCompat.from(context)
                    val repliedNotification = NotificationCompat.Builder(context, id)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources, R.mipmap.ic_launcher
                            )
                        )
                        .setDeleteIntent(myFrag.mDeletePendingIntent)  //so we know if they deleted it.
                        .setContentText("Replied")
                        .setChannelId(NotiActivity.id)
                        .setOnlyAlertOnce(true)  //don't sound/vibrate/lights again!
                        .build()
                    notificationManager.notify(conversationId, repliedNotification)
                    myFrag.NotificationReply(conversationId, replyMessage)
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noti)
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            myFrag = NotiFragment()
            transaction.replace(R.id.container, myFrag)
            transaction.commit()
        }
        createchannel()  //setup channels if needed.
    }


    override fun onStart() {
        super.onStart()
        registerReceiver(mDeleteReceiver, IntentFilter(ACTION_NOTIFICATION_DELETE))
        registerReceiver(mReadReceiver, IntentFilter(READ_ACTION))
        registerReceiver(mReplyReceiver, IntentFilter(REPLY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mDeleteReceiver)
        unregisterReceiver(mReadReceiver)
        unregisterReceiver(mReplyReceiver)
    }


    private fun createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel(
                id,
                "channel name", //name of the channel
                NotificationManager.IMPORTANCE_DEFAULT
            )   //importance level

            mChannel.description = "description"
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.setShowBadge(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            nm.createNotificationChannel(mChannel)

        }
    }

    companion object {
        var id = "test_channel_01"


        val ACTION_NOTIFICATION_DELETE = "duc.delete"
        val READ_ACTION = "duc.ACTION_MESSAGE_READ"
        val REPLY_ACTION = "duc.ACTION_MESSAGE_REPLY"
        val CONVERSATION_ID = "conversation_id"
        val EXTRA_REMOTE_REPLY = "extra_remote_reply"
    }

}