package com.hd.videoEditor.model


import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoData(
    var height: Int = 0,
    var rotation: Int = 0,
    var width: Int = 0,
    var bitmap: Bitmap? = null,
    var duration: Int = 0
) : Parcelable