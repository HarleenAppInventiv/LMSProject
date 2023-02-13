package com.selflearningcoursecreationapp.ui.moderator.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestCountModel(
    @SerializedName("acceptedRequestCount")
    var acceptedRequestCount: Int? = 0,
    @SerializedName("acceptedRequestId")
    var acceptedRequestId: Int? = null,
    @SerializedName("coAuthorRequestCount")
    var coAuthorRequestCount: Int? = 0,
    @SerializedName("coAuthorRequestId")
    var coAuthorRequestId: Int? = null,
    @SerializedName("moderatorCommentsCount")
    var moderatorCommentsCount: Int = 0,
    @SerializedName("moderatorCommentsId")
    var moderatorCommentsId: Int? = null,
    @SerializedName("rejectedCoursesCount")
    var rejectedCoursesCount: Int? = 0,
    @SerializedName("rejectedCoursesId")
    var rejectedCoursesId: Int? = null,
    @SerializedName("rejectedRequestCount")
    var rejectedRequestCount: Int? = 0,
    @SerializedName("rejectedRequestId")
    var rejectedRequestId: Int? = null,
    @SerializedName("sentRequestCount")
    var sentRequestCount: Int? = 0,
    @SerializedName("sentRequestId")
    var sentRequestId: Int? = null,
    @SerializedName("paymentWithdrawRequestCount")
    var paymentWithdrawRequestCount: Int? = null

) : Parcelable