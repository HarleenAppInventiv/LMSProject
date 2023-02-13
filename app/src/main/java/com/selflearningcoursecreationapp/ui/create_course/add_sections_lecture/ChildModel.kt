@file:Suppress("SuspiciousVarProperty")

package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Parcelable
import android.text.Html
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChildModel(
    @SerializedName("lectureId")
    var lectureId: Int? = null,
    @SerializedName("mediaType")
    var mediaType: Int? = null,
    @SerializedName("contentStatus")
    var contentStatus: Int? = null,
    @SerializedName("lectureContentType")
    var lectureContentType: String? = null,
    var isEnabled: Boolean = true,
    var sectionId: Int = 0,
    var courseTitle: String? = null,
    var courseDesc: String? = null,
    var courseCategory: String? = null,
    var sectionTitle: String? = null,
    var sectionDesc: String? = null,

//
    @SerializedName("deleted", alternate = ["updated"])
    var deletedUpdatedStatus: Boolean? = null,

    @SerializedName("lectureStatusId", alternate = ["status"])
    var lectureStatusId: Int? = null,
    @SerializedName("lectureStatus")
    var lectureStatus: String? = null,
    @SerializedName("lectureContentId")
    var lectureContentId: String? = "",
    @SerializedName("lectureContentStatus")
    var lectureContentStatus: String? = "",


    @SerializedName("lectureTranscriptUrl")
    var lectureTranscriptUrl: String? = null,

    @SerializedName("lectureComment")
    var lectureComment: String? = null,

    @SerializedName("courseCommentCreatedDate", alternate = ["lectureCommentCreatedDate"])
    val courseCommentCreatedDate: String? = null,

    @SerializedName("lectureCommentId")
    var lectureCommentId: Int? = null,

    @SerializedName("lectureThumbnailId")
    var lectureThumbnailId: String? = "",
    @SerializedName("quizId")
    var quizId: Int? = null,
    @SerializedName("lectureContentStatusId")
    var lectureContentStatusId: Int? = null,

    @SerializedName("success")
    var success: Boolean? = false,


    @SerializedName("lectureContentUrl")
    var lectureContentUrl: String? = null,

    var lectureContentSize: Long? = null,

    @SerializedName("totalQuizQuestionCount")
    var totalQuizQues: Int? = null,

    @SerializedName("inCompleteQuiz")
    var allAnsMarked: Boolean = false,

    @SerializedName("lectureIsCompleted")
    var lectureIsCompleted: Boolean? = false,

    @SerializedName("lecturePercentageCompleted")
    var lecturePercentageCompleted: Double? = null,

    @SerializedName("lectureTotalPlayedTime")
    var lectureTotalPlayedTime: Long? = null,

    @SerializedName("lectureSignLanguageContentDuration")
    var lectureSignLanguageContentDuration: Long? = null,


    @SerializedName("signLanguageStreamingEndpointUrl")
    var signLanguageStreamingEndpointUrl: String? = null,

    @SerializedName("lectureLastPlayedTime")
    var lectureLastPlayedTime: Long? = null,


    @SerializedName("streamingEndpointUrl")
    var streamingEndpointUrl: String? = null,

    @SerializedName("lectureSignLanguageThumbnailUrl")
    var lectureSignLanguageThumbnailUrl: String? = null,

    @SerializedName("lectureSignLanguageThumbnailBlurHash")
    var lectureSignLanguageThumbnailBlurHash: String? = null,

    @SerializedName("lectureSignLanguageTranscriptUrl")
    var lectureSignLanguageTranscriptUrl: String? = null,

    @SerializedName("attemptedId")
    var attemptedId: Int? = null,

    @SerializedName("quizPassed")
    var quizPassed: Boolean? = false,

//    var lectureContentUrl: String? = null,

    @SerializedName("lectureThumbnailUrl")
    var lectureThumbnailUrl: String? = null,

    ) :
    BaseObservable(), Parcelable {

    @SerializedName("lectureContentName")
    var lectureContentName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.docLesson)

        }

    @SerializedName("textFileText")
    var textFileText: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.textLesson)

        }

    @SerializedName("lectureTitle", alternate = ["title"])
    var lectureTitle: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.docLesson)
            notifyPropertyChanged(BR.textLesson)
            notifyPropertyChanged(BR.audioLesson)
            notifyPropertyChanged(BR.titleLength)

        }


    @Transient
    @get:Bindable
    var titleLength: Int = 0
        get() =
            if (lectureTitle.isNullOrEmpty()) 0 else lectureTitle!!.trim().length


    @SerializedName("lectureContentDuration")
    var lectureContentDuration: Long? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.docLesson)
            notifyPropertyChanged(BR.textLesson)

        }

    @SerializedName(
        "thumbNailURl",
        alternate = ["thumbnailContentName", "thumbnailContentUrl", "thumbnailUrl"]
    )
    var thumbNailURl: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.thumbNailURls)

        }

    @Transient
    @get:Bindable
    var docLesson: Boolean = false
        get() =
            !lectureTitle.isNullOrEmpty() && !lectureContentDuration.isNullOrZero() && !lectureContentName.isNullOrEmpty()


    @Transient
    @get:Bindable
    var textLesson: Boolean = false
        get() =
            !lectureTitle.isNullOrEmpty() && !lectureContentDuration.isNullOrZero() && !textFileText.isNullOrEmpty()


    @Transient
    @get:Bindable
    var audioLesson: Boolean = false
        get() = !lectureTitle.isNullOrEmpty()

    @Transient
    @get:Bindable
    var thumbNailURls: Boolean = false
        get() = !thumbNailURl.isNullOrEmpty()

    @Transient
    @get:Bindable
    var lectureIsEmpty: Boolean = false
        get() = !lectureTitle.isNullOrEmpty()


    fun isDocValid(): Int {
        return when {
            lectureContentName.isNullOrEmpty() || lectureContentName.isNullOrBlank() -> {
                R.string.select_doc_file
            }
            lectureTitle.isNullOrBlank() -> {
                R.string.please_enter_title
            }
//            lectureContentDuration.isNullOrZero() -> {
//                R.string.enter_reading_time
//            }
            lectureContentDuration.isNullOrZero() -> {
                R.string.reading_time_should_be_greater_than_0
            }
            else -> {
                0
            }
        }
    }

    fun getLessonText(): String {
        return if (textFileText.isNullOrEmpty()) "" else Html.fromHtml(textFileText!!).toString()
    }

    fun isTextValid(): Int {
        return when {
            textFileText.isNullOrBlank() -> {
                R.string.please_enter_lesson
            }
            lectureTitle.isNullOrBlank() -> {
                R.string.please_enter_title
            }

            lectureContentDuration.isNullOrZero() -> {
                R.string.reading_time_should_be_greater_than_0
            }
            else -> {
                0
            }
        }
    }

    fun isAudioValid(): Int {
        return when {
            lectureTitle.isNullOrBlank() -> {
                R.string.please_enter_title
            }
            else -> {
                0
            }
        }
    }

}
