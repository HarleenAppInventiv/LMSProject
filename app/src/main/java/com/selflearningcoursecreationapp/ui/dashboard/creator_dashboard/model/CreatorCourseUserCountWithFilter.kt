package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model

import com.google.gson.annotations.SerializedName

data class CreatorCourseUserCountWithFilter(
    @SerializedName("list") val list: ArrayList<CourseList>? = null,
    @SerializedName("totalCount") val totalCount: Int? = 0,
    @SerializedName("pageNumber") val pageNumber: Int? = 0,
    @SerializedName("pageSize") val pageSize: Int? = 0,
    @SerializedName("resultCount") val resultCount: Int? = 0
)

data class CourseList(

    @SerializedName("courseId") val courseId: Int,
    @SerializedName("courseTitle") val courseTitle: String,
    @SerializedName("totalEnrolledUser") val totalEnrolledUser: Int
)