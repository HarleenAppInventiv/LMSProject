package com.selflearningcoursecreationapp.data.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.utils.ACTION_NOTIFICATION_BROADCAST
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var token = ""

    @SuppressLint("WrongThread")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val bundle = Bundle()
        for ((key, value) in remoteMessage.data) {
            bundle.putString(key, value)

        }
        Log.d("varun", "onMessageReceived: ${remoteMessage.data}")
        val isAppInForeground =
            ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        generateNotification(applicationContext, bundle, isAppInForeground)

        if (isAppInForeground) {
            val intent = Intent(ACTION_NOTIFICATION_BROADCAST)
            intent.putExtras(bundle)
            LocalBroadcastManager.getInstance(SelfLearningApplication.applicationContext())
                .sendBroadcast(intent)
        } else {
            Log.d("varun", "onMessageReceived:false")
        }


    }


    @SuppressLint("InvalidWakeLockTag", "UnspecifiedImmutableFlag")
    fun generateNotification(context: Context, extra: Bundle, isAppInForeground: Boolean) {
        val message = extra.get("body") as String?

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(extra.get("title") as String?)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setLargeIcon(largeIcon)

        val notifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mBuilder.setSound(notifySound)
        val vibrate = longArrayOf(0, 100, 200, 300)
        mBuilder.setVibrate(vibrate)
        mBuilder.setAutoCancel(true)

        var resultIntent: Intent? = if (extra.get("userType").toString().toInt() == 3) Intent(
            context,
            ModeratorActivity::class.java
        ) else {
            Log.d("varun", "generateNotification: yoooo ")
            Intent(context, HomeActivity::class.java)
        }
        try {
            val r = RingtoneManager.getRingtone(applicationContext, notifySound)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        resultIntent?.action = Intent.ACTION_MAIN
        resultIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
        resultIntent?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        resultIntent?.putExtra("notificationBundle", extra)

        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        mBuilder.setWhen(System.currentTimeMillis())
        mBuilder.setContentIntent(resultPendingIntent)
        val mPowerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        var wl: PowerManager.WakeLock? = null
        if (mPowerManager != null) {
            wl = mPowerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "tag"
            )
        }
        wl?.acquire(1000)
        val mNotificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val chan1 = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "notification_channel_default", NotificationManager.IMPORTANCE_DEFAULT
            )
            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            chan1.setShowBadge(true)
            mNotificationManager.createNotificationChannel(chan1)
        }

        if (extra.containsKey("image-url")) {
            val bitmap: Bitmap? = getBitmapFromUrl(extra.get("image-url") as String?)
            mBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setLargeIcon(bitmap)
        }

        mNotificationManager.notify(0, mBuilder.build())
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG)
                Log.e("awesome", "Error in getting notification image: " + e.localizedMessage)
            null
        }
    }


    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "SLCC_notification"

    }

}