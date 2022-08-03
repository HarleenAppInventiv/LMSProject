package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterOptionData(
    @SerializedName("filterOptionCount")
    var filterOptionCount: Int? = null,

    @SerializedName("filterOptionDisplayName")
    var filterOptionDisplayName: String? = null,

    @SerializedName("filterName")
    var filterName: String? = null,
    @SerializedName("filterOptionId")
    var filterOptionId: Int? = null,
    @SerializedName("filterOptionOperatorId")
    var filterOptionOperatorId: Int? = null,
    @SerializedName("filterOptionValue")
    var filterOptionValue: String? = null,
    @Transient
    var isSelected: Boolean = false
) : Parcelable


