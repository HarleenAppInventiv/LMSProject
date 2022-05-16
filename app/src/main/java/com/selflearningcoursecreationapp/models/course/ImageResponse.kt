package com.selflearningcoursecreationapp.models.course


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageResponse(
    @SerializedName("blurHash")
    var blurHash: String? = null,
    @SerializedName("contentType")
    var contentType: String? = null,
    @SerializedName("fileName")
    var fileName: String? = null,
    @SerializedName("filePath")
    var filePath: String? = null,
    @SerializedName("fileType")
    var fileType: String? = null,
    @SerializedName("fileUrl")
    var fileUrl: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("originalFileName")
    var originalFileName: String? = null
) : Parcelable