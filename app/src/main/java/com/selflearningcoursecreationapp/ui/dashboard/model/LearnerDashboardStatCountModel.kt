package com.selflearningcoursecreationapp.ui.dashboard.model

import com.google.gson.annotations.SerializedName

data class LearnerDashboardStatCountModel(
    @SerializedName("completecoursE_COUNT") var completecoursE_COUNT: Int? = 0,
    @SerializedName("enrolledcoursE_COUNT") var enrolledcoursE_COUNT: Int? = 0,
    @SerializedName("favouritecoursE_COUNT") var favouritecoursE_COUNT: Int? = 0,
    @SerializedName("todocoursE_COUNT") var todocoursE_COUNT: Int? = 0,
    @SerializedName("donecoursE_COUNT") var donecoursE_COUNT: Int? = 0,
    @SerializedName("inprogresscoursE_COUNT") var inprogresscoursE_COUNT: Int? = 0
)