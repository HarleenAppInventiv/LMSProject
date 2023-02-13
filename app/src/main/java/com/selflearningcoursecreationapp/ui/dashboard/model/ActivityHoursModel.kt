package com.selflearningcoursecreationapp.ui.dashboard.model

import com.google.gson.annotations.SerializedName


data class ActivityHoursModel(

    @field:SerializedName("totalRequestedHours")
    val totalRequestedHours: Long? = null,
    @field:SerializedName("startDate")
    val startDate: String? = null,
    @field:SerializedName("endDate")
    val endDate: String? = null,

    @field:SerializedName("totalActivityHours")
    val totalActivityHours: Long? = null
)
