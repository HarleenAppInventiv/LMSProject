package com.selflearningcoursecreationapp.ui.course_details.model

import com.google.gson.annotations.SerializedName

data class AddReviewRequestModel(

    @field:SerializedName("rating")
    var rating: Int? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("courseId")
    var courseId: Int? = null
)
