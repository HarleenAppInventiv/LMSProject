package com.selflearningcoursecreationapp.ui.profile.requestTracker


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestCountModel(
    @SerializedName("acceptedRequestCount")
    var acceptedRequestCount: Int? = null,
    @SerializedName("acceptedRequestId")
    var acceptedRequestId: Int? = null,
    @SerializedName("coAuthorRequestCount")
    var coAuthorRequestCount: Int? = null,
    @SerializedName("coAuthorRequestId")
    var coAuthorRequestId: Int? = null,
    @SerializedName("moderatorCommentsCount")
    var moderatorCommentsCount: Int = 0,
    @SerializedName("moderatorCommentsId")
    var moderatorCommentsId: Int? = null,
    @SerializedName("rejectedCoursesCount")
    var rejectedCoursesCount: Int? = null,
    @SerializedName("rejectedCoursesId")
    var rejectedCoursesId: Int? = null,
    @SerializedName("rejectedRequestCount")
    var rejectedRequestCount: Int? = null,
    @SerializedName("rejectedRequestId")
    var rejectedRequestId: Int? = null,
    @SerializedName("sentRequestCount")
    var sentRequestCount: Int? = null,
    @SerializedName("sentRequestId")
    var sentRequestId: Int? = null
) : Parcelable