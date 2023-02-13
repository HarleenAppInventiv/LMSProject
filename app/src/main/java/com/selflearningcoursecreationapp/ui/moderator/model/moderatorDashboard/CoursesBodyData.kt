package com.selflearningcoursecreationapp.ui.moderator.model.moderatorDashboard

import com.google.gson.annotations.SerializedName

data class CoursesBodyData(

    @SerializedName("searchFields") val searchFields: List<SearchFields>,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("moderatorStatus") val moderatorStatus: Int
)


data class SearchFields(

    @SerializedName("fieldName") val fieldName: String,
    @SerializedName("fieldValue") val fieldValue: List<String>,
    @SerializedName("operatorType") val operatorType: Int
)