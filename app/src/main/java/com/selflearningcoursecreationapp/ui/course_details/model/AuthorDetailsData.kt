package com.selflearningcoursecreationapp.ui.course_details.model

import com.google.gson.annotations.SerializedName

data class AuthorDetailsData(

    @SerializedName("userId") var userId: Int? = 0,
    @SerializedName("totalCount") var totalCount: Int? = 0,
    @SerializedName("pageNumber") var pageNumber: Int? = 0,
    @SerializedName("userName") var userName: String? = "",
    @SerializedName("bio") var bio: String? = "",
    @SerializedName("profileBlurHash") var profileBlurHash: String? = "",
    @SerializedName("profileUrl") var profileUrl: String? = "",
    @SerializedName("courseCount") var courseCount: Int? = 0,
    @SerializedName("authorProfileCourses") var authorProfileCourses: ArrayList<AuthorProfileCourses>? = null
)
