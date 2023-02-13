package com.hd.videoEditor.ffmpeg

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FFmpegStateData(
    var error: String? = null,
    var operationCode: String? = null,
    var observed: Boolean = false,
    var state: Int? = null,
    var time: Int? = null
) : Parcelable