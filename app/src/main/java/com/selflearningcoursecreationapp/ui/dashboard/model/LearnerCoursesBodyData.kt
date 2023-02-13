package com.selflearningcoursecreationapp.ui.dashboard.model

import com.google.gson.annotations.SerializedName

data class LearnerCoursesBodyData(

    @SerializedName("searchFields") val searchFields: List<LearnerSearchFields>,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("courseType") val courseType: Int
)


data class LearnerSearchFields(

    @SerializedName("fieldName") val fieldName: String,
    @SerializedName("fieldValue") val fieldValue: List<String>,
    @SerializedName("operatorType") val operatorType: Int
)