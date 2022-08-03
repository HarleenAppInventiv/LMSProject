package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterResponse(
    @SerializedName("filterName")
    var filterName: String? = null,
    @SerializedName("screenId")
    var screenId: Int? = null,
    @SerializedName("filters")
    var list: ArrayList<FilterTypeData>? = null
) : Parcelable