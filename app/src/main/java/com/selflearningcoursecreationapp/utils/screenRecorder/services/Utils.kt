package com.selflearningcoursecreationapp.utils.screenRecorder.services

import android.R
import android.content.ContentValues
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.utils.screenRecorder.data.DataManager
import com.selflearningcoursecreationapp.utils.screenRecorder.settings.PreferenceHelper
import java.io.File

fun PreferenceHelper.generateOptions(dataManager: DataManager): Options? {
    val saveUri = saveLocation ?: return null
    val folderUri = saveUri.uri


    val uri = dataManager.create(folderUri, filename, "video/mp4", null) ?: return null

    return Options(
        video = VideoOptions(
            resolution = resolution.run { Resolution(first, second) },
            bitrate = videoBitrate,
            encoder = videoEncoder,
            fps = fps,
            virtualDisplayDpi = displayMetrics.densityDpi
        ),
        audio =
        AudioOptions.RecordAudio(
            source = MediaRecorder.AudioSource.MIC,
            samplingRate = audioSamplingRate,
            encoder = audioEncoder,
            bitRate = audioBitrate
        ),
        output = OutputOptions(
            uri = SaveUri(uri, saveUri.type)
        )
    )


}

fun saveFileLocally(

): Uri {
    val root = Environment.getExternalStorageDirectory().toString()

    val app_folder = "$root/Self/"
    val uri: Uri

    val filePrefix = "Self_"
    val fileExtn = ".pdf"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            filePrefix + System.currentTimeMillis() + fileExtn
        )
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, R.attr.mimeType)
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOWNLOADS + "/Self"
        )
        contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())



        uri = getAppContext().contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )!!
    } else {

        var dest = File(File(app_folder), filePrefix + fileExtn)
        var fileNo = 0
        while (dest.exists()) {
            fileNo++
            dest = File(File(app_folder), filePrefix + fileNo + fileExtn)

        }
        uri = FileProvider.getUriForFile(
            getAppContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            dest
        )


    }

    return uri
}
