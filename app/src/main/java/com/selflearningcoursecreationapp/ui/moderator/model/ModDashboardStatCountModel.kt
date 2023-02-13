package com.selflearningcoursecreationapp.ui.moderator.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModDashboardStatCountModel(
    @SerializedName("acceptedcoursE_COUNT")
    var acceptedcoursE_COUNT: Int? = 0,
    @SerializedName("pendingcoursE_COUNT")
    var pendingcoursE_COUNT: Int? = 0,
    @SerializedName("rejectedcoursE_COUNT")
    var rejectedcoursE_COUNT: Int? = 0

) : Parcelable