package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model

import com.google.gson.annotations.SerializedName

data class CreatorAudienceStateCount(
    @SerializedName("totaluseR_COUNT") var totaluseR_COUNT: Int? = 0,
    @SerializedName("totalcoursE_COUNT") var totalcoursE_COUNT: Int? = 0,
    @SerializedName("totalvideoS_COUNT") var totalvideoS_COUNT: Int? = 0
)