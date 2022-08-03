@file:Suppress("SuspiciousVarProperty")

package com.selflearningcoursecreationapp.models.course

import android.os.Parcelable
import android.text.Html
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.wordCount
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.MediaType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseData(

    @SerializedName("categoryId")
    var categoryId: Int? = null,

    @SerializedName("courseId")
    var courseId: Int? = 0,

    @SerializedName("createdById")
    var createdById: Int? = 0,

    @SerializedName("languageId")
    var languageId: Int? = null,
    @SerializedName("makeQuizMandatory", alternate = ["quizMandatory"])
    var makeQuizMandatory: Boolean? = null,
    @SerializedName("assessmentMandatory")
    var assessmentMandatory: Boolean? = null,
    @SerializedName("assessmentFreezeContent")
    var assessmentFreezeContent: Boolean? = null,
    @SerializedName("assessmentPassingCriteria")
    var assessmentPassingCriteria: Int? = null,
    @SerializedName("statusId")
    var statusId: Int? = null,

    @SerializedName("courseTypeId", alternate = ["courseType"])
    var courseTypeId: Int? = null,

    @SerializedName("targetAudienceId", alternate = ["targetAudience"])
    var targetAudienceId: Int? = null,
    @SerializedName("courseDuration", alternate = ["courseDurations"])
    var courseDuration: Long? = null,

    @SerializedName(
        "courseComplexityId",
        alternate = ["courseComplexity", "courseComplexityTypeId"]
    )
    var courseComplexityId: Int? = null,

    @SerializedName("assessmentId")
    var assessmentId: Int? = null,
    @SerializedName("approvalStatus")
    var approvalStatus: Int? = null,
    @SerializedName("courseStatus")
    var courseStatus: Int? = null,
    @SerializedName("completeStep")
    var completeStep: Int? = null,

    @SerializedName("assessmentName")
    var assessmentName: String? = null,
    @SerializedName("razorpayKey")
    var razorpayKey: String? = null,
    @SerializedName("orderId")
    var orderId: String? = null,
    @SerializedName("currency")
    var currency: String? = null,
    @SerializedName("notes")
    var notes: String? = null,
    @SerializedName("currencyId")
    var currencyId: Int? = null,
    @SerializedName("paymentStatus")
    var paymentStatus: Int? = null,

    @SerializedName("name")
    var name: String? = null,
    @SerializedName("courseBannerHash", alternate = ["courseBannerBlurHash"])
    var courseBannerHash: String? = null,
    @SerializedName("courseLogoHash", alternate = ["courseLogoBlurHash"])
    var courseLogoHash: String? = null,
    @SerializedName("keywords")
    var keywords: ArrayList<String?>? = null,

    @SerializedName("passingCriteria", alternate = ["quizPassingCriteria"])
    var passingCriteria: Int? = null,

    @SerializedName("freezeContent", alternate = ["quizFreezeContent"])
    var freezeContent: Boolean? = null,


    @SerializedName("courseCoAuthors", alternate = ["authors"])
    var courseCoAuthors: ArrayList<UserProfile>? = null,

    @SerializedName("averageRating")
    var averageRating: String? = null,

    @SerializedName("userCourseStatus")
    var userCourseStatus: Int? = null,

    @SerializedName("reviewId")
    var reviewId: Int? = null,

    @SerializedName("totalReviews")
    var totalReviews: Long? = null,

    @SerializedName("createdByName", alternate = ["createdName", "prefill_Name"])
    var createdByName: String? = null,
    @SerializedName("prefill_Email")
    var prefillEmail: String? = null,
    @SerializedName("prefill_Contact")
    var prefillContact: String? = null,

    @SerializedName("themeId")
    var themeId: String? = null,

    @SerializedName("courseWishlisted")
    var courseWishlisted: Int? = null,
    @SerializedName("totalSections")
    var totalSections: Int? = 0,

    @SerializedName("profileUrl", alternate = ["image", "profileURL"])
    var profileUrl: String? = null,

    @SerializedName("description")
    var contentDescription: String? = null,
    @SerializedName("createdDate")
    var createdDate: String? = null,
    @SerializedName("currencySymbol")
    var currencySymbol: String? = null,

    @SerializedName("courseRating")
    var courseRating: Int? = null,
    @SerializedName("userDisLiked")
    var userDisLiked: Int? = null,

    @SerializedName("totalLikes")
    var totalLikes: Int? = null,
    @SerializedName("totalDislikes")
    var totalDislikes: Int? = null,

    @SerializedName("userLiked")
    var userLiked: Int? = null,

    @Transient
    var isPaid: Boolean = false,
    @Transient
    var isCreator: Boolean = false,
    @Transient
    var isCoAuthor: Boolean = false,
    @Transient
    var coAuthorId: Int = 0,

    ) : BaseObservable(), Parcelable {
    @SerializedName("courseTitle")
    var courseTitle: String? = null

    @SerializedName("sections")
    var sectionData: ArrayList<SectionModel>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
            notifyPropertyChanged(BR.sectionDataAdded)
        }

    @SerializedName("targetAudiences")
    var targetAudiences: ArrayList<SingleChoiceData>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseDescription")
    var courseDescription: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }


    @SerializedName("courseFee", alternate = ["amount"])
    var courseFee: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }


    @SerializedName("rewardPoints")
    var rewardPoints: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }


    @SerializedName("categoryName", alternate = ["categoryTypeName"])
    var categoryName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("keyTakeaways", alternate = ["keyTakeAways"])
    var keyTakeaways: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("languageName")
    var languageName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseBannerUrl", alternate = ["courseBannerContentURL"])
    var courseBannerUrl: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseBannerId")
    var courseBannerId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseLogoId")
    var courseLogoId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseLogoUrl")
    var courseLogoUrl: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseTypeName")
    var courseTypeName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("targetAudienceName")
    var targetAudienceName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseComplexityTypeName")
    var courseComplexityName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }


    @Transient
    var currentPage: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }


    @Transient
    @get:Bindable
    var allDataEntered: Boolean = true
        get() {

            return when (currentPage) {
                0 -> {
                    !courseTitle.isNullOrEmpty() /*&& courseTitle!!.isNotBlank()*/ && getDescription().isNotEmpty() && !categoryName.isNullOrEmpty() && !languageName.isNullOrEmpty()
                            && getTakeaways().isNotEmpty()
                }
                1 -> {
                    !courseTypeName.isNullOrEmpty()
                            && !targetAudiences.isNullOrEmpty()
                            && !courseComplexityName.isNullOrEmpty()
                            && !courseFee.isNullOrEmpty()
                            && !courseBannerUrl.isNullOrEmpty()
                            && !courseLogoUrl.isNullOrEmpty()
                            && if (courseTypeId == CourseType.REWARD_POINTS) !rewardPoints.isNullOrEmpty() else true
                }
                2 -> {
                    val creatorId =
                        if (isCreator) createdById else if (isCoAuthor) coAuthorId else 0
                    !sectionData.isNullOrEmpty() && sectionData?.find {
                        it.sectionCreatedById == coAuthorId && !it.isSaved || it.isDataValid(
                            true, creatorId ?: 0
                        ) != 0
                    } == null
                }
                3 -> {
                    true
                }
                else -> true
            }
        }

    @Bindable
    @Transient
    var sectionDataAdded: Boolean = false
        get() {
            notifyPropertyChanged(BR.allDataEntered)
            val creatorId = if (isCreator) createdById else if (isCoAuthor) coAuthorId else 0
            return !sectionData.isNullOrEmpty()
                    && sectionData?.find {
                it.sectionCreatedById == coAuthorId && !it.isSaved || it.isDataValid(
                    true,
                    creatorId ?: 0
                ) != 0
            } == null

        }


    fun isStep1Verified(): Int {
        return when {
            courseTitle?.trim().isNullOrEmpty() -> {
                R.string.please_enter_title
            }
            courseDescription.isNullOrBlank() -> {
                R.string.please_enter_desc
            }
            categoryName.isNullOrBlank() -> {
                R.string.please_select_category
            }
            languageName.isNullOrBlank() -> {
                R.string.please_select_language
            }
            else -> {
                return 0
            }

        }
    }

    fun getCreatedName(): String {
        return createdByName?.trim() ?: ""
    }

    fun isStep2Verified(): Int {
        return when {
            courseTypeId.isNullOrZero() -> {
                R.string.please_select_course_type
            }
            targetAudiences.isNullOrEmpty() -> {
                R.string.please_select_target_audience
            }
            courseComplexityName.isNullOrEmpty() -> {
                R.string.please_select_course_complexity
            }
            courseFee.isNullOrEmpty() -> {
                R.string.please_enter_course_fee
            }
            isPaid && courseFee?.toDoubleOrNull().isNullOrZero() -> R.string.plz_enter_course_fee

//            courseTypeId == 4 -> if (rewardPoints.isNullOrEmpty())
//                    return R.string.plz_enter_reward_points else 1


//            rewardPoints.isNullOrEmpty() -> {
//                   R.string.plz_enter_reward_points
//               }


            courseTypeId == CourseType.REWARD_POINTS && rewardPoints!!.toIntOrNull()
                .isNullOrZero() -> R.string.reward_points_cant_be_zero
            else -> {
                return 0
            }

        }
    }

//    fun isStep3Verified(): Int {
//        return when {
//            sectionData.isNullOrEmpty() -> R.string.plz_add_sections
//            sectionData!!.find { !it.uploadLesson } != null -> R.string.plz_add_data_in_section
//            sectionData!!.find { it.lessonList.isNullOrEmpty() } != null -> R.string.plz_add_lesson
//            !sectionData!!.filter { it.lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && it.totalQuizQues.isNullOrZero() } != null }
//                .isNullOrEmpty() -> R.string.plz_add_ques_in_quiz_section
//            !sectionData!!.filter { it.lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && !it.allAnsMarked } != null }
//                .isNullOrEmpty() -> R.string.plz_mark_ans_ques_in_quiz
//            !sectionData!!.filter { it.lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && it.lectureTitle.isNullOrEmpty() } != null }
//                .isNullOrEmpty() -> R.string.plz_add_data_in_quiz_section
//            sectionData!!.find { !it.isSaved } != null -> R.string.plz_save_sections
//            sectionData!!.find { it.changesMade } != null -> R.string.plz_save_sections
//            else -> 0
//        }
//    }
//
//    fun isStep3CoAuthorVerified(loggedId: Int?, checkId: Boolean = true): Int {
//
//        return when {
//            sectionData.isNullOrEmpty() -> R.string.plz_add_sections
//            sectionData!!.find {
//                !it.uploadLesson && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            } != null -> R.string.plz_add_data_in_section
//            sectionData!!.find {
//                it.lessonList.isNullOrEmpty() && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            } != null -> R.string.plz_add_lesson
//            !sectionData!!.filter {
//                it.lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && it.totalQuizQues.isNullOrZero() } != null && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            }
//                .isNullOrEmpty() -> R.string.plz_add_ques_in_quiz_section
//            !sectionData!!.filter {
//                it.lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && !it.allAnsMarked } != null && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            }
//                .isNullOrEmpty() -> R.string.plz_mark_ans_ques_in_quiz
//            !sectionData!!.filter {
//                it.lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && it.lectureTitle.isNullOrEmpty() } != null && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            }
//                .isNullOrEmpty() -> R.string.plz_add_data_in_quiz_section
//            sectionData!!.find {
//                !it.isSaved && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            } != null -> R.string.plz_save_sections
//            sectionData!!.find {
//                it.changesMade && if (checkId) {
//                    it.sectionCreatedById == loggedId
//                } else true
//            } != null -> R.string.plz_save_sections
//            else -> 0
//        }
//    }


    fun isStep3Verified(loggedId: Int?, checkId: Boolean = true): Int {
        var errorId = 0
        sectionData?.forEach {
            val selection = if (checkId) {
                it.sectionCreatedById == loggedId
            } else true
            if (selection) {

                errorId = isStep3SingleVerified(it)
                if (errorId != 0) {
                    return@forEach
                }
            }

        }
        return errorId
    }

    private fun isStep3SingleVerified(data: SectionModel): Int {
        return when {
            !data.uploadLesson -> R.string.plz_add_data_in_section
            data.lessonList.isNullOrEmpty() -> R.string.plz_add_lesson
            data.lessonList.find { it.mediaType == MediaType.QUIZ && it.totalQuizQues.isNullOrZero() } != null -> R.string.plz_add_ques_in_quiz_section
            data.lessonList.find { it.mediaType == MediaType.QUIZ && !it.allAnsMarked } != null -> R.string.plz_mark_ans_ques_in_quiz
            data.lessonList.find { it.mediaType == MediaType.QUIZ && it.lectureTitle.isNullOrEmpty() } != null -> R.string.plz_add_data_in_quiz_section
            !data.isSaved -> R.string.plz_save_sections
//            data.changesMade -> R.string.plz_save_sections
            else -> 0
        }
    }


    fun getWordCount(): Int {
        return if (courseDescription.isNullOrEmpty()) 0 else (Html.fromHtml(courseDescription)
            .toString()).wordCount()
    }


    fun getKeyCount(): Int {
        return if (keyTakeaways.isNullOrEmpty()) 0 else (Html.fromHtml(keyTakeaways)
            .toString()).wordCount()
    }

    fun getDescription(): String {
        return if (courseDescription.isNullOrEmpty()) "" else (Html.fromHtml(courseDescription)
            .toString()).trim()
    }

    fun getTakeaways(): String {
        return if (keyTakeaways.isNullOrEmpty()) "" else (Html.fromHtml(keyTakeaways)
            .toString()).trim()
    }


    fun getAudienceName(): String {
        return targetAudiences?.map { it.title }?.joinToString() ?: ""
    }


    fun getPublishCourseData(): CourseData {

        return CourseData().also {
            it.courseId = courseId
            it.keywords = keywords
        }
    }


    fun getCoAuthor(id: Int): UserProfile? {
        return courseCoAuthors?.find { it.id == id && it.isActive == true }
    }


}


data class WishListResponse(
    @SerializedName("list")
    val list: ArrayList<CourseData>,
    @SerializedName("totalCount")
    val totalCount: Int? = 0,

    @SerializedName("totalReviews")
    val totalReviews: Float? = 0f,

    @SerializedName("averageReview")
    val averageReview: Float? = 0f,

    @SerializedName("userAlreadyRated")
    val userAlreadyRated: Boolean,

    )

data class Rating(
    val averageReview: Float,
    val totalReviews: Int,
)
