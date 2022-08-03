package com.selflearningcoursecreationapp.utils.screenRecorder

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.selflearningcoursecreationapp.R

const val RECORDING_NOTIFICATION_CHANNEL_ID = "channel_recording"
const val FINISH_NOTIFICATION_CHANNEL_ID = "channel_finished"

@TargetApi(26)
fun Context.createNotificationChannels() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return
    }
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .createNotificationChannels(
            listOf(
                channelRecording(),
                channelFinish()
            )
        )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.channelRecording() = NotificationChannel(
    RECORDING_NOTIFICATION_CHANNEL_ID,
    getString(R.string.notification_channel_recording),
    NotificationManager.IMPORTANCE_LOW
).apply {
    enableLights(true)
    lightColor = Color.RED
    setShowBadge(false)
    enableVibration(false)
    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.channelFinish() = NotificationChannel(
    FINISH_NOTIFICATION_CHANNEL_ID,
    getString(R.string.notification_channel_finish),
    NotificationManager.IMPORTANCE_DEFAULT
).apply {
    //enableLights(true)
    setShowBadge(true)
    enableVibration(false)
    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
}
