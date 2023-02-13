package com.selflearningcoursecreationapp.models.course

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadMetaData(
    @SerializedName("contentId")
    var contentId: String? = null,
) : Parcelable