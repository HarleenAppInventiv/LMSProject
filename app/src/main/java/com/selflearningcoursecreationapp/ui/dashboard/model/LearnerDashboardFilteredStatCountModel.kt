package com.selflearningcoursecreationapp.ui.dashboard.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LearnerDashboardFilteredStatCountModel(
    @SerializedName("todocoursE_COUNT")
    var todocoursE_COUNT: Int? = 0,
    @SerializedName("donecoursE_COUNT")
    var donecoursE_COUNT: Int? = 0,
    @SerializedName("inprogresscoursE_COUNT")
    var inprogresscoursE_COUNT: Int? = 0

) : Parcelable