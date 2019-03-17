package com.example.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.RemoteInput
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class NotiFragment : Fragment() {
    private var mNotificationManager: NotificationManager? = null
    private var mNotificationManagerCompat: NotificationManagerCompat? = null

    private var TAG = "myFrag"

    lateinit var mDeletePendingIntent: PendingIntent

    private lateinit var mNumberOfNotifications: TextView
    private lateinit var logger: TextView

    private var NotificationNum = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_noti, container, false)
        mNotificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManagerCompat = NotificationManagerCompat.from(activity!!.applicationContext)


        val deleteIntent = Intent(NotiActivity.ACTION_NOTIFICATION_DELETE)
        mDeletePendingIntent = PendingIntent.getBroadcast(
            activity,
            REQUEST_CODE, deleteIntent, 0
        )


        mNumberOfNotifications = view.findViewById(R.id.numNoti)

        logger = view.findViewById(R.id.logger)

        view.findViewById<Button>(R.id.addbutton).setOnClickListener{ createNotification() }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        updateNumberOfNotifications()
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun updateNumberOfNotifications() {
        val numberOfNotifications = mNotificationManager!!.activeNotifications.size

        mNumberOfNotifications.text = "Number of Active notifications is: $numberOfNotifications"
        Log.i(TAG, "Number of Active notifications is: $numberOfNotifications")
    }

    private fun getMessageReadIntent(id: Int): Intent {
        return Intent()
            .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            .setAction(NotiActivity.READ_ACTION)
            .putExtra(NotiActivity.CONVERSATION_ID, id)
    }

    private fun getMessageReplyIntent(conversationId: Int): Intent {
        return Intent()
            .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            .setAction(NotiActivity.REPLY_ACTION)
            .putExtra(NotiActivity.CONVERSATION_ID, conversationId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    internal fun createNotification() {
        val readPendingIntent = PendingIntent.getBroadcast(
            activity!!.applicationContext,
            NotificationNum,
            getMessageReadIntent(NotificationNum),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val choices = arrayOf("No", "Yes", "Maybe", "Go away!")
        val remoteInput = RemoteInput.Builder(NotiActivity.EXTRA_REMOTE_REPLY)
            .setLabel("Reply")
            .setChoices(choices)
            .build()

        val replyIntent = PendingIntent.getBroadcast(
            activity!!.applicationContext,
            NotificationNum,
            getMessageReplyIntent(NotificationNum),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val actionReplyByRemoteInput = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher, "Reply", replyIntent
        )
            .addRemoteInput(remoteInput)
            .build()


        val unreadConvBuilder = NotificationCompat.CarExtender.UnreadConversation.Builder("Jim ")
            .setLatestTimestamp(System.currentTimeMillis())
            .setReadPendingIntent(readPendingIntent)
            .setReplyAction(replyIntent, remoteInput)


        val builder = NotificationCompat.Builder(activity!!.applicationContext, NotiActivity.id)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    activity!!.applicationContext.resources, R.mipmap.ic_launcher
                )
            )
            .setContentText("Are you working?")
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Jim ")
            .setContentIntent(readPendingIntent)
            .setDeleteIntent(mDeletePendingIntent)
            .setChannelId(NotiActivity.id)
            .extend(
                NotificationCompat.CarExtender()
                    .setUnreadConversation(unreadConvBuilder.build())
                    .setColor(ContextCompat.getColor(requireContext(), R.color.abc_background_cache_hint_selector_material_dark))
            )
            .addAction(actionReplyByRemoteInput)

        logger.append("Sending notification $NotificationNum\n")

        mNotificationManagerCompat!!.notify(NotificationNum, builder.build())
        NotificationNum++
        //update the number of notifications.
        updateNumberOfNotifications()
    }


    fun NotificationRead(id: Int) {
        logger.append("Notification " + id + "has been read\n")
    }

    fun NotificationReply(id: Int, message: String) {
        logger.append("Notification $id: reply is $message\n")
    }

    companion object {
        private val REQUEST_CODE = 2323
    }
}