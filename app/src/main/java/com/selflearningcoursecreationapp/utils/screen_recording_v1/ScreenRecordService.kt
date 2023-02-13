package com.selflearningcoursecreationapp.utils.screen_recording_v1

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.ACTION_RECORDER_BROADCAST
import com.selflearningcoursecreationapp.utils.screenRecorder.TimerWidgetService
import com.selflearningcoursecreationapp.utils.screenRecorder.home.FloatingWidgetService


class ScreenRecordService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ScreenRecordService_nofity"
        private const val NOTIFICATION_CHANNEL_NAME = "ScreenRecordService"
        private const val NOTIFICATION_CHANNEL_DESC = "ScreenRecordService"
        private const val NOTIFICATION_ID = 1000
        private const val NOTIFICATION_TICKER = "RecorderApp"
        private val ACTION_STOP_SERVICE: String? = "skillfy_stop_service"
    }


    private var width: Int? = null
    private var height: Int? = null

    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private val tempDisplayMetrics = DisplayMetrics()


    private val screenRecorder by lazy {
        windowManager.defaultDisplay.getRealMetrics(tempDisplayMetrics)
        ScreenRecorder(
            this,
            tempDisplayMetrics.widthPixels,
            tempDisplayMetrics.heightPixels,
            tempDisplayMetrics.densityDpi,
        ).apply {
            videoCallback = {
                try {
                    val recorderIntent = Intent(ACTION_RECORDER_BROADCAST)
                    recorderIntent.putExtras(bundleOf("recordingStatus" to 3))
                    LocalBroadcastManager.getInstance(SelfLearningApplication.applicationContext())
                        .sendBroadcast(recorderIntent)
//                stopService(Intent(this@ScreenRecordService,FloatingWidgetService::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var screenAction: ScreenAction? = null
    private var largeIconId = 0
    private var smallIconId = 0

    override fun onBind(intent: Intent?): IBinder? = null

    //    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ACTION_STOP_SERVICE.equals(intent?.action)) {
            showLog("SCREEN_RECORDER", "called to cancel service")
            height = intent?.getIntExtra("height", 1080) ?: 1024
            width = intent?.getIntExtra("width", 720) ?: 720
            notificationManager?.cancel(NOTIFICATION_ID)
            try {
                val recorderIntent = Intent(ACTION_RECORDER_BROADCAST)
                recorderIntent.putExtras(bundleOf("recordingStatus" to 3))
                LocalBroadcastManager.getInstance(SelfLearningApplication.applicationContext())
                    .sendBroadcast(recorderIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stopSelf()
        }
        val command = intent?.getIntExtra("command", 1) ?: 1
        if (command == -1) {
            return super.onStartCommand(intent, flags, startId)
        }

        when (command) {
            1 -> {

                screenAction?.stop()
                screenAction = null

                // start recording screen
                screenAction = screenRecorder
                largeIconId = intent?.getIntExtra("largeIconId", 0) ?: 0
                smallIconId = intent?.getIntExtra("smallIconId", 0) ?: 0
                val resultData: Intent? = intent?.getParcelableExtra("data")
                if (resultData != null) {
                    screenAction?.init()
                    createNotification()
                    screenAction?.start(
                        resultData,
                        intent.getIntExtra("height", 0), intent.getIntExtra("width", 0)
                    )
                }
            }

            3 -> {
                // stop action
//                if (::screenAction.isInitialized) {
//                    screenAction.stop()
//                }
                screenAction?.stop()
                screenAction = null
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        try {

            screenAction?.stop()
            screenAction = null
        } catch (e: Exception) {
            showException(e)
        }
        Toast.makeText(this, getString(R.string.video_saved), Toast.LENGTH_SHORT).show()
        stopService(Intent(this, FloatingWidgetService::class.java))
        stopService(Intent(this, TimerWidgetService::class.java))
        super.onDestroy()
    }

    var notificationManager: NotificationManager? = null

    //    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createNotification() {
        showLog("SCREEN_RECORDER", "notification: " + Build.VERSION.SDK_INT)
        try {
            val notificationIntent = Intent(this, ScreenRecordService::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

            val stopSelf = Intent(this, ScreenRecordService::class.java)
            stopSelf.action = ACTION_STOP_SERVICE
            val pStopSelf =
                PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_IMMUTABLE)

            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Screen recorder service")
                    .setContentText("Screen recorder service")
                    .setTicker(NOTIFICATION_TICKER)
                    .addAction(R.drawable.ic_stop, "Stop Recording", pStopSelf)
                    .setContentIntent(pendingIntent)
            if (largeIconId > 0) {
                notificationBuilder.setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        largeIconId
                    )
                )
            }
            if (smallIconId > 0) {
                notificationBuilder.setSmallIcon(smallIconId)
            }
            val notification: Notification = notificationBuilder.build()
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel.description = NOTIFICATION_CHANNEL_DESC
            }
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


inline val Resources.screenWidth: Int
    get() = displayMetrics.widthPixels

inline val Context.screenWidth: Int
    get() = resources.screenWidth


inline val Resources.screenHeight: Int
    get() = displayMetrics.heightPixels

inline val Context.screenHeight: Int
    get() = resources.screenHeight


inline val screenWidthNew: Int
    get() = SelfLearningApplication.applicationContext().screenWidth

inline val screenHightNew: Int
    get() = SelfLearningApplication.applicationContext().screenHeight