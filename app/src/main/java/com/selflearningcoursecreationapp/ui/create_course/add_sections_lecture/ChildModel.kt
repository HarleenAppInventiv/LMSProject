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
    @SerializedName("lectureStatusId")
    var lectureStatusId: Int? = null,
    @SerializedName("lectureStatus")
    var lectureStatus: String? = null,
    @SerializedName("lectureContentId")
    var lectureContentId: String? = "",
    @SerializedName("lectureThumbnailId")
    var lectureThumbnailId: String? = "",
    @SerializedName("quizId")
    var quizId: Int? = null,
    var lectureContentUrl: String? = null,
    var lectureContentSize: Long? = null,
    @SerializedName("totalQuizQuestionCount")
    var totalQuizQues: Int? = null,

    @SerializedName("inCompleteQuiz")
    var allAnsMarked: Boolean = false,


    ) :
    BaseObservable(), Parcelable {

    @SerializedName("lectureContentName")
    var lectureContentName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.docLesson)

        }

    @SerializedName("textFileText")
    var textFileText: String? = null
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

        }

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


    fun isDocValid(): Int {
        return when {
            lectureContentName!!.isBlank() -> {
                R.string.select_doc_file
            }
            lectureTitle!!.isBlank() -> {
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
            textFileText!!.isBlank() -> {
                R.string.please_enter_lesson
            }
            lectureTitle!!.isBlank() -> {
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
            lectureTitle!!.isBlank() -> {
                R.string.please_enter_title
            }
            else -> {
                0
            }
        }
    }

}
