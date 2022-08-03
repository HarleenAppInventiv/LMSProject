package com.selflearningcoursecreationapp.utils.screenRecorder.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recording(
    val uri: Uri,
    val title: String,
    val duration: Int, // seconds
    val size: Long, // bytes
    val modified: Long, // millis
    val isPending: Boolean = false
) : Parcelable