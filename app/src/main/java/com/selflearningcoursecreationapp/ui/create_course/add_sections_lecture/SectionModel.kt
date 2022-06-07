package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SectionModel(
    @SerializedName("sectionNumber")
    var sectionNumber: Int? = null,
    @SerializedName("sectionCreatedByName")
    var sectionCreatedByName: String? = null,
    @SerializedName("sectionLogoURL")
    var sectionLogoURL: String? = null,
    @SerializedName("sectionCreatedByProfileURL")
    var sectionCreatedByProfileURL: String? = null,
    @SerializedName("sectionId")
    var sectionId: Int? = null,
    @SerializedName("sectionCreatedById")
    var sectionCreatedById: Int? = null,
    var expandedItemPos: Int = -1,
    @SerializedName("sectionDuration")
    var sectionDuration: Long = 0L,
    var locked: Boolean = false,
    var isSaved: Boolean = false,
    var isVisible: Boolean = true,
) : BaseObservable(), Parcelable {


    @SerializedName("sectionTitle")
    var sectionTitle: String? = null
        set(value) {
            field = value
            changesMade = true
            notifyPropertyChanged(BR.changesMade)
            notifyPropertyChanged(BR.uploadLesson)
//            notifyChange()
        }

    @SerializedName("lectures")
    var lessonList: ArrayList<ChildModel> = arrayListOf<ChildModel>()
        set(value) {
            field = value
            notifyPropertyChanged(BR.uploadLesson)
        }

    @SerializedName("sectionDescription")
    var sectionDescription: String? = null
        set(value) {
            field = value
            changesMade = true
            notifyPropertyChanged(BR.changesMade)
            notifyPropertyChanged(BR.uploadLesson)
//            notifyChange()

        }


    @Transient
    @get:Bindable
    var uploadLesson: Boolean = false
        get() = !sectionTitle.isNullOrEmpty() && !sectionDescription.isNullOrEmpty() && !lessonList.isNullOrEmpty()

    @Transient
    @get:Bindable
    var changesMade: Boolean = false


    fun isDataValid(checkChanges: Boolean, currentId: Int = 0, ignoreId: Boolean = false): Int {
        return when {
            !ignoreId && currentId != sectionCreatedById -> 0
            sectionTitle.isNullOrEmpty() -> R.string.plz_enter_title
            sectionDescription.isNullOrEmpty() -> R.string.plz_enter_description
            lessonList.isNullOrEmpty() -> R.string.plz_upload_lesson
            lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && it.totalQuizQues.isNullOrZero() } != null -> R.string.plz_add_ques_in_quiz
            lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && !it.allAnsMarked } != null -> R.string.plz_mark_ans_ques_in_quiz
            lessonList.find { it.mediaType == MEDIA_TYPE.QUIZ && it.lectureTitle.isNullOrEmpty() } != null -> R.string.plz_add_data_in_quiz
            checkChanges && changesMade -> R.string.plz_save_sections
            else -> 0
        }
    }
}

