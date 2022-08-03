package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.models.course.CourseData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseTypeModel(
    @SerializedName("courses")
    var courses: List<CourseData>? = null,
    @SerializedName("coursesType")
    var coursesType: Int? = null,
    @SerializedName("orderId")
    var orderId: Int? = null,
    @SerializedName("title")
    var title: String? = null,
) : Parcelable
