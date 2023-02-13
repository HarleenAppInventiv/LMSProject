package com.selflearningcoursecreationapp.ui.course_details.model

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthorProfileCourses(

    @SerializedName("courseId") var courseId: Int? = 0,
    @SerializedName("courseTitle") var courseTitle: String? = "",
    @SerializedName("createdById") var createdById: Int? = 0,
    @SerializedName("createdByName") var createdByName: String? = "",
    @SerializedName("courseTypeId") var courseTypeId: Int? = 0,
    @SerializedName("averageRating") var averageRating: Float? = 0f,
    @SerializedName("totalReviews") var totalReviews: Int? = 0,
    @SerializedName("totalSections") var totalSections: Int? = 0,
    @SerializedName("totalLectures") var totalLectures: Int? = 0,
    @SerializedName("courseFee") var courseFee: Float? = 0f,
    @SerializedName("courseDurations") var courseDurations: Long? = 0,
    @SerializedName("categoryTypeId") var categoryTypeId: Int? = 0,
    @SerializedName("categoryTypeName") var categoryTypeName: String? = "",
    @SerializedName("languageId") var languageId: Int? = 0,
    @SerializedName("languageName") var languageName: String? = "",
    @SerializedName("courseLogoContentId") var courseLogoContentId: String? = "",
    @SerializedName("courseLogoContentURL") var courseLogoContentURL: String? = "",
    @SerializedName("courseBannerContentId") var courseBannerContentId: String? = "",
    @SerializedName("courseBannerContentURL") var courseBannerContentURL: String? = "",
    @SerializedName("courseLogoContentBlurHash") var courseLogoContentBlurHash: String? = "",
    @SerializedName("courseBannerContentBlurHash") var courseBannerContentBlurHash: String? = "",

    @SerializedName("status") var status: Int? = 0,
    @SerializedName("paymentStatus") var paymentStatus: Int? = 0,
    @SerializedName("rewardPoints") var rewardPoints: Int? = 0,
    @SerializedName("userCourseStatus") var userCourseStatus: Int? = 0,
    @SerializedName("percentageCompleted") var percentageCompleted: Float? = 0f,
    @SerializedName("totalDurationLeft") var totalDurationLeft: Int? = 0,
    @SerializedName("currencyCode") var currencyCode: String? = "",
    @SerializedName("currencySymbol") var currencySymbol: String? = "",
    @SerializedName("publishDate") var publishDate: String? = ""
) : BaseObservable(), Parcelable {
    @get:Bindable
    @SerializedName("courseWishlisted")
    var courseWishlisted: Int? = null
}
