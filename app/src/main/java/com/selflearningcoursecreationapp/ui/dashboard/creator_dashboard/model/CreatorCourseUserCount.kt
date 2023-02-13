package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model

import com.google.gson.annotations.SerializedName

data class CreatorCourseUserCount(
    @SerializedName("inactivecoursE_COUNT") var inactivecoursE_COUNT: Int? = 0,
    @SerializedName("completedcoursE_COUNT") var completedcoursE_COUNT: Int? = 0,
    @SerializedName("inprogresscoursE_COUNT") var inprogresscoursE_COUNT: Int? = 0
)