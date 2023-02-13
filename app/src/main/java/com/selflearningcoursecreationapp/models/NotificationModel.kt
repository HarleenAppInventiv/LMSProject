package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class NotificationModel(
    @SerializedName("list")
    val list: ArrayList<NotificationData>?
)

data class NotificationData(
    @SerializedName("courseId")
    val courseId: Int? = null,
    @SerializedName("desciption")
    val desciption: String? = null,
    @SerializedName("image_Url")
    val imageUrl: String? = null,
    @SerializedName("isRead")
    var isRead: Boolean? = null,
    @SerializedName("notificationId")
    val notificationId: Int? = null,
    @SerializedName("notification_Payload")
    val notificationPayload: String? = null,
    @SerializedName("payload")
    val payload: String? = null,
    @SerializedName("sentDate")
    val sentDate: String? = null,

    @SerializedName("title")
    val title: String? = null,
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("totalreadnotificationcount")
    val totalreadnotificationcount: Int? = null,

    @SerializedName("totalunreadnotificationcount", alternate = ["NOTIFICATIONUNREADCOUNT"])
    val totalunreadnotificationcount: Int? = null,

    @SerializedName("updated")
    val updated: Boolean? = null,
    @SerializedName("deleted")
    val deleted: Boolean? = null
)

@Parcelize
data class NotificationPayload(
    @SerializedName("CategoryId")
    val categoryId: Int? = null,

    @SerializedName("CategoryName")
    val categoryName: Int? = null,

    @SerializedName("CategoriesForModerator")
    var CategoriesForModerator: Int? = null,


    ) : Parcelable