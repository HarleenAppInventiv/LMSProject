package com.selflearningcoursecreationapp.models.course


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllCoursesResponse(
    @SerializedName("pageNumber")
    var currentPage: Int? = null,
    @SerializedName("resultCount")
    var resultCount: Int? = null,
    @SerializedName("pageSize")
    var pageSize: Int? = null,
    @SerializedName("totalCount")
    var totalCount: Int? = null,
    @SerializedName("courses", alternate = ["list"])
    var coursesList: ArrayList<CourseData>? = null
) : Parcelable