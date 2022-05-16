package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChildModel(
    var lessonNumber: Int?,
    var lectureId: Int?,
    var lectureTitle: String?,
    var mediaType: Int?,
) :
    BaseObservable(), Parcelable