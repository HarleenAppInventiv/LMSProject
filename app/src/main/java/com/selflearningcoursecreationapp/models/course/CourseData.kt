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
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseData(

    @SerializedName("categoryId")
    var categoryId: Int? = 0,

    @SerializedName("courseId")
    var courseId: Int? = 0,

    @SerializedName("languageId")
    var languageId: Int? = 0,

    @SerializedName("courseTypeId")
    var courseTypeId: Int? = null,

    @SerializedName("targetAudienceId")
    var targetAudienceId: Int? = null,

    @SerializedName("courseComplexityId")
    var courseComplexityId: Int? = null,

    @SerializedName("searchKeywords")
    var searchKeywords: String? = "",
    @SerializedName("courseBannerHash")
    var courseBannerHash: String? = "",
    @SerializedName("courseLogoHash")
    var courseLogoHash: String? = "",


    @SerializedName("rewardPoints")
    var rewardPoints: Boolean? = null,
    var isPaid: Boolean = false


) : BaseObservable(), Parcelable {
    @SerializedName("courseTitle")
    var courseTitle: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseDescription")
    var courseDescription: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseFee")
    var courseFee: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("categoryName")
    var categoryName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("keyTakeaways")
    var keyTakeaways: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("languageName")
    var languageName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseBannerUrl")
    var courseBannerUrl: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseLogoUrl")
    var courseLogoUrl: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseTypeName")
    var courseTypeName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("targetAudienceName")
    var targetAudienceName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.allDataEntered)
        }

    @SerializedName("courseComplexityName")
    var courseComplexityName: String? = ""
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
//            return true
            return when (currentPage) {
                0 -> {
                    !courseTitle.isNullOrEmpty() && !Html.fromHtml(courseDescription)?.trim()
                        .isNullOrEmpty() && !categoryName.isNullOrEmpty() && !languageName.isNullOrEmpty()
                }
                1 -> {
                    !courseTypeName.isNullOrEmpty()
                            && !targetAudienceName.isNullOrEmpty()
                            && !courseComplexityName.isNullOrEmpty()
                            && !courseFee.isNullOrEmpty()
                            && !courseBannerUrl.isNullOrEmpty()
                            && !courseLogoUrl.isNullOrEmpty()
                            && !Html.fromHtml(keyTakeaways)?.trim().isNullOrEmpty()
                }
                else -> true
            }
        }


    fun isStep1Verified(): Int {
        return when {
            courseTitle.isNullOrEmpty() -> {
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


    fun isStep2Verified(): Int {
        return when {
            courseTypeId.isNullOrZero() -> {
                R.string.please_select_course_type
            }
            targetAudienceId.isNullOrZero() -> {
                R.string.please_select_target_audience
            }
            courseComplexityName.isNullOrEmpty() -> {
                R.string.please_select_course_complexity
            }
//            searchKeywords.isBlank() -> {
//                R.string.please_select_search_keyword
//            }
            courseFee.isNullOrEmpty() -> {
                R.string.please_enter_course_fee
            }
            isPaid && courseFee!!.toDoubleOrNull().isNullOrZero() -> R.string.plz_enter_course_fee
            else -> {
                return 0
            }

        }
    }

    fun getWordCount(): Int {
        return (Html.fromHtml(courseDescription).toString()).wordCount()
    }
}
