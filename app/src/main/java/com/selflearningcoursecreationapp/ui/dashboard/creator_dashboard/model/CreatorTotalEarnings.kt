package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model

import com.google.gson.annotations.SerializedName

data class CreatorTotalEarnings(
    @SerializedName("totalEarning") val totalEarning: Float? = 0f,
    @SerializedName("totalRangeEarning") val totalRangeEarning: Float? = 0f,
    @SerializedName("courseEarningDates") val courseEarningDates: List<CourseEarningDates>? = null
)

data class CourseEarningDates(

    @SerializedName("index") val index: Int,
    @SerializedName("modifiedDate") val modifiedDate: String,
    @SerializedName("courseId") val courseId: Int,
    @SerializedName("todayEarning") val todayEarning: Float
)