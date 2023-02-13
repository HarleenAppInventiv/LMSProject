package com.hd.videoEditor.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import com.hd.videoEditor.model.VideoData


fun String?.fetchVideoInfo(): VideoData? {
    if (isNullOrEmpty()) {
        return null
    }
    val videoData = VideoData()
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this)
    videoData.width =
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            ?: 0
    videoData.height =
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
            ?: 0
    videoData.rotation =
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull()
            ?: 0

    videoData.duration =
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toIntOrNull() ?: 0

    videoData.bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    Logd("FETCH_VIDEO", "width >> ${videoData.width}")
    Logd("FETCH_VIDEO", "width >> ${videoData.height}")
    Logd("FETCH_VIDEO", "width >> ${videoData.rotation}")
    Logd("FETCH_VIDEO", "width >> ${videoData.duration}")

    return videoData
}

fun Long?.getTime(context: Context, showHms: Boolean = true, showHrs: Boolean = true): String {
    if (showHms) {
        return getTimeInHMS(showHrs)
    } else {
        return context.getTimeInChar(this)
    }


}

private fun Long?.getTimeInHMS(showHrs: Boolean): String {
    if (this.isNullOrZero()) {
        return if (showHrs) "00:00:00" else "00:00"
    }
    val seconds = this!!.div(1000).rem(60).toInt()
    val mins = this.div(1000.times(60)).rem(60).toInt()
    val hrs = this.div(1000.times(60).times(60)).rem(24).toInt()




    return "${if (hrs == 0 && !showHrs) "" else if (hrs > 9) hrs.toString() + ":" else "0$hrs:"}${if (mins > 9) mins else "0$mins"}:${if (seconds > 9) seconds else "0$seconds"}"

}

fun Context.getTimeInChar(milliseconds: Long?): String {
    if (milliseconds.isNullOrZero()) {
        return ""
    }


    val seconds = milliseconds!!.div(1000).rem(60).toInt()
    val mins = milliseconds.div(1000.times(60)).rem(60).toInt()
    val hrs = milliseconds.div(1000.times(60).times(60)).rem(24).toInt()

    Logd("hr", hrs.toString())
    var time = ""
    if (hrs >= 0) {
        time = if (hrs > 9) hrs.toString() else "0$hrs"

    }
    Logd("hr1", time.toString())
    if (mins >= 0) {
        time += ":${if (mins > 9) mins else "0$mins"}"

    }
    Logd("min", time.toString())
    if (seconds >= 0) {
        time += ":${if (seconds > 9) seconds else "0$seconds"}"
    }
    Logd("sec", time.toString())


    return String.format("%s", if (time.startsWith(".")) time.substring(1) else time)


}

fun Long?.isNullOrZero(): Boolean {
    return this == null || this == 0L
}

