package com.selflearningcoursecreationapp.ui.course_details.ratings.model

import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.models.course.CourseData

data class SocketReviewsResponse(

    @field:SerializedName("Type")
    val type: String? = null,

    @field:SerializedName("Message")
    val message: String? = null,

    @field:SerializedName("Data")
    val data: CourseData? = null,

    @field:SerializedName("StatusCode")
    val statusCode: Int? = null,

    @field:SerializedName("Success")
    val success: Boolean? = null
)

//data class Data(
//
//	@field:SerializedName("Description")
//	val description: Any? = null,
//
//	@field:SerializedName("TotalDislikes")
//	val totalDislikes: Int? = null,
//
//	@field:SerializedName("TotalReviews")
//	val totalReviews: Any? = null,
//
//	@field:SerializedName("UserAlreadyRated")
//	val userAlreadyRated: Boolean? = null,
//
//	@field:SerializedName("Count")
//	val count: Int? = null,
//
//	@field:SerializedName("TotalLikes")
//	val totalLikes: Int? = null,
//
//	@field:SerializedName("UserDisLiked")
//	val userDisLiked: Int? = null,
//
//	@field:SerializedName("ProfileBlurHash")
//	val profileBlurHash: Any? = null,
//
//	@field:SerializedName("Name")
//	val name: Any? = null,
//
//	@field:SerializedName("CourseRating")
//	val courseRating: Double? = null,
//
//	@field:SerializedName("CourseId")
//	val courseId: Int? = null,
//
//	@field:SerializedName("UserId")
//	val userId: Int? = null,
//
//	@field:SerializedName("AvatarId")
//	val avatarId: Any? = null,
//
//	@field:SerializedName("AverageRating")
//	val averageRating: Any? = null,
//
//	@field:SerializedName("CreatedDate")
//	val createdDate: String? = null,
//
//	@field:SerializedName("ReviewId")
//	val reviewId: Int? = null,
//
//	@field:SerializedName("UserLiked")
//	val userLiked: Int? = null,
//
//	@field:SerializedName("ProfileURL")
//	val profileURL: Any? = null
//)
