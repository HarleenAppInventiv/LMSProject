package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SectionModel(
//    var title: String = "",
    var sectionId: Int? = null,
    var expandedItemPos: Int? = -1,
//    var desc: String = "",
    var lessonList: ArrayList<ChildModel> = arrayListOf<ChildModel>(),
    var isVisible: Boolean = true
) : BaseObservable(), Parcelable {


    @SerializedName("name")
    var name: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.docLesson)

        }

    @SerializedName("title", alternate = ["lectureTitle"])
    var title: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.uploadLesson)
            notifyPropertyChanged(BR.docLesson)
        }

    @SerializedName("desc")
    var desc: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.uploadLesson)
            notifyPropertyChanged(BR.docLesson)

        }

    @SerializedName("readingTime")
    var readingTime: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.docLesson)

        }

    @Transient
    @get:Bindable
    var uploadLesson: Boolean = false
        get() = !title.isNullOrEmpty() && !desc.isNullOrEmpty()


    @Transient
    @get:Bindable
    var docLesson: Boolean = false
        get() = !title.isNullOrEmpty() && !readingTime.isNullOrEmpty() && !name.isNullOrEmpty()


    fun isDocValid(): Int {
        return when {
            name!!.isBlank() -> {
                R.string.select_doc_file
            }
            title!!.isBlank() -> {
                R.string.please_enter_title
            }
            readingTime!!.isBlank() -> {
                R.string.enter_reading_time
            }
            else -> {
                0
            }
        }
    }
}

