package com.selflearningcoursecreationapp.ui.moderator.model.moderatorDashboard

import com.google.gson.annotations.SerializedName

data class FilteredCoursesBodyData(

    @SerializedName("searchFields") val searchFields: List<FilteredSearchFields>,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int
)


data class FilteredSearchFields(

    @SerializedName("fieldName") val fieldName: String,
    @SerializedName("fieldValue") val fieldValue: List<String>,
    @SerializedName("operatorType") val operatorType: Int
)