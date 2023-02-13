package com.selflearningcoursecreationapp.utils.downloadManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData
import com.selflearningcoursecreationapp.utils.downloadManager.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class FileDownloadService : Service() {
    var DOWNLOAD_THREAD_POOL_SIZE = 1
    private var sectionId = 0
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        fileUrl = intent.getStringExtra("streamingUrl")
        DBCourseData = intent.getParcelableExtra("DBCourseData")
        offlineCourseData = intent.getParcelableExtra("offlineCourseData")
        sectionId = intent.getIntExtra("sectionId", 0)
        downloadManager = ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE)
        displayNotification()
        var fileName = URLUtil.guessFileName(
            fileUrl, null, null
        )
        var fileExtention = getFileExtension(fileName)
        offlineCourseData?.sectionList?.get(0)?.lessonList?.get(0)?.fileExtention = fileExtention
        fileName = getFileNameWithoutExtension(fileName)
        downloadManager?.add(
            DownloadRequest(Uri.parse(fileUrl))
                .setDestinationURI(
                    Uri.parse(
                        getExternalFilesDir("").toString() + fileName
                    )
                ).setPriority(DownloadRequest.Priority.HIGH)
                .setRetryPolicy(DefaultRetryPolicy())
                .setDownloadContext("Download1")
                .setStatusListener(object : DownloadStatusListenerV1 {
                    override fun onDownloadComplete(downloadRequest: DownloadRequest) {
                        GlobalScope.launch {
                            saveToDB(downloadRequest.destinationURI.path.toString())
                        }
                    }

                    override fun onDownloadFailed(
                        downloadRequest: DownloadRequest,
                        errorCode: Int,
                        errorMessage: String
                    ) {
                        try {


                            notificationManager?.cancel(NOTIFICATION_ID)
                            val intent = Intent(DOWNLOAD_COMPLETE_BROADCAST)
                            intent.putExtra("downloadCompleted", false)
                            LocalBroadcastManager.getInstance(applicationContext)
                                .sendBroadcast(intent)
                            notificationManager?.cancel(NOTIFICATION_ID)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onProgress(
                        downloadRequest: DownloadRequest,
                        totalBytes: Long,
                        downloadedBytes: Long,
                        progress: Int
                    ) {
                        notificationBuilder?.setProgress(100, progress, false);
                        // Displays the progress bar for the first time.
                        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build());
                        Log.e("progress", "progress>>>> " + progress.toString() + "")
                    }
                })
        )
        return START_STICKY
    }

    val FILE_EXTENSION_SEPARATOR = "."

    fun getFileNameWithoutExtension(filePath: String): String? {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val extenPosi: Int = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR)
        val filePosi: Int = filePath.lastIndexOf(File.separator)
        if (filePosi == -1) {
            return if (extenPosi == -1) filePath else filePath.substring(
                0,
                extenPosi
            )
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1)
        }
        return if (filePosi < extenPosi) filePath.substring(
            filePosi + 1,
            extenPosi
        ) else filePath.substring(filePosi + 1)
    }

    fun getFileExtension(filePath: String): String? {
        if (TextUtils.isEmpty(filePath)) {
            return filePath
        }
        val extenPosi: Int = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR)
        val filePosi: Int = filePath.lastIndexOf(File.separator)
        if (filePosi == -1) {
            return if (extenPosi == -1) filePath else filePath.substring(
                extenPosi,
                filePath.length
            )
        }
        return ""

    }

    private fun displayNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_NAME,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.enableVibration(false)
            channel.setSound(null, null)
            notificationManager?.createNotificationChannel(channel)
        }
        notificationBuilder = NotificationCompat.Builder(
            applicationContext, CHANNEL_NAME
        )
        notificationBuilder?.setContentTitle(CHANNEL_DESC)?.setSound(null)
            ?.setSmallIcon(R.mipmap.ic_launcher)
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
    }

    private suspend fun saveToDB(path: String) {
        offlineCourseData?.sectionList?.get(0)?.lessonList?.get(0)?.filePath = path
        val mDatabase = (application as SelfLearningApplication).getAppDB()
        var sectionPosition = -1
        if (DBCourseData != null && DBCourseData!!.sectionList != null && DBCourseData!!.sectionList!!.size > 0) {
            sectionPosition = getSectionPositionInDB(DBCourseData!!.sectionList)
            if (sectionPosition > -1) {
                offlineCourseData?.sectionList?.get(0)?.lessonList?.get(0)?.let { offlineCourse ->
                    DBCourseData?.sectionList?.get(sectionPosition)?.lessonList?.add(
                        offlineCourse
                    )
                }

            } else {
                offlineCourseData?.sectionList?.get(0)?.let { DBCourseData?.sectionList?.add(it) }
            }
            mDatabase.getDao().updateCourse(DBCourseData);
        } else {
            mDatabase.getDao().insertCourseData(offlineCourseData);
        }

        try {

            val intent = Intent(DOWNLOAD_COMPLETE_BROADCAST)
            intent.putExtra("downloadCompleted", true)
            LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(intent)
            notificationManager?.cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSectionPositionInDB(sectionList: ArrayList<OfflineSectionData>?): Int {
        sectionList?.let {
            for (i in sectionList.indices) {


//        for (i in 0 until sectionList!!.size) {
                if (sectionList[i].sectionId == sectionId) {
                    return i
                }
            }
        }

        return -1
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private var fileUrl: String? = null
        private var offlineCourseData: OfflineCourseData? = null
        private var DBCourseData: OfflineCourseData? = null
        var notificationBuilder: NotificationCompat.Builder? = null
        var downloadManager: ThinDownloadManager? = null
        var notificationManager: NotificationManager? = null
    }
}
