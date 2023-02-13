package com.selflearningcoursecreationapp.ui.course_details.ratings.model

import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.models.course.CourseData


data class ReviewResponse(

    @field:SerializedName("resultCount")
    val resultCount: Int? = null,

    @field:SerializedName("totalReviews")
    val totalReviews: Int? = null,

    @field:SerializedName("pageNumber")
    val pageNumber: Int? = null,

    @field:SerializedName("averageReview")
    val averageReview: Double? = null,

    @field:SerializedName("pageSize")
    val pageSize: Int? = null,

    @field:SerializedName("userAlreadyRated")
    val userAlreadyRated: Boolean? = null,

    @field:SerializedName("list")
    val list: ArrayList<CourseData> = arrayListOf(),

    @field:SerializedName("totalCount")
    val totalCount: Int? = null
)
