import com.google.gson.annotations.SerializedName

data class LearnerDashboardDataList(

    @SerializedName("courseId") var courseId: Int,
    @SerializedName("courseTitle") var courseTitle: String,
    @SerializedName("courseFee") var courseFee: Int,
    @SerializedName("courseDurations") var courseDurations: Int,
    @SerializedName("totalSections") var totalSections: Int,
    @SerializedName("totalLectures") var totalLectures: Int,
    @SerializedName("averageRating") var averageRating: Double,
    @SerializedName("totalReviews") var totalReviews: Int,
    @SerializedName("createdById") var createdById: Int,
    @SerializedName("createdByName") var createdByName: String,
    @SerializedName("categoryTypeId") var categoryTypeId: Int,
    @SerializedName("categoryTypeName") var categoryTypeName: String,
    @SerializedName("languageId") var languageId: Int,
    @SerializedName("languageName") var languageName: String,
    @SerializedName("courseComplexityTypeId") var courseComplexityTypeId: Int,
    @SerializedName("courseComplexityTypeName") var courseComplexityTypeName: String,
    @SerializedName("courseTypeId") var courseTypeId: Int,
    @SerializedName("courseTypeName") var courseTypeName: String,
    @SerializedName("courseLogoContentId") var courseLogoContentId: String,
    @SerializedName("courseLogoContentURL") var courseLogoContentURL: String,
    @SerializedName("courseBannerContentId") var courseBannerContentId: String,
    @SerializedName("courseBannerContentURL") var courseBannerContentURL: String,
    @SerializedName("courseWishlisted") var courseWishlisted: Int,
    @SerializedName("userCourseStatus") var userCourseStatus: Int,
    @SerializedName("status") var status: Int,
    @SerializedName("paymentStatus") var paymentStatus: Int,
    @SerializedName("rewardPoints") var rewardPoints: Int,
    @SerializedName("publishedDate") var publishedDate: String,
    @SerializedName("percentageCompleted") var percentageCompleted: Double? = null,
    @SerializedName("totalDurationLeft") var totalDurationLeft: Int? = 0,
    @SerializedName("isSignLanguage")
    var isSignLanguage: Boolean? = null,
    @SerializedName("totalPlayedTime")
    var totalPlayedTime: Int? = null,
)