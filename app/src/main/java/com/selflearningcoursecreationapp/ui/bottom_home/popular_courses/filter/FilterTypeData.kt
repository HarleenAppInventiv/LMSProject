package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterTypeData(
    @SerializedName("filterDisplayName")
    var filterDisplayName: String? = null,
    @SerializedName("filterName")
    var filterName: String? = null,
    @SerializedName("filterId")
    var filterId: Int? = null,

    @SerializedName("filterType")
    var filterType: Int? = null,
    @SerializedName("options")
    var filterOptionData: ArrayList<FilterOptionData>? = null
) : Parcelable

@Parcelize
data class SelectedFilterData(
    @SerializedName("operatorType", alternate = ["filterOptionOperatorId"])
    var filterOptionOperatorId: Int? = null,
    @SerializedName("fieldValue", alternate = ["filterOptionValue"])
    var filterOptionValue: String? = null,
    @SerializedName("fieldName", alternate = ["filterName"])
    var filterName: String? = null,
    @SerializedName("filterOptionId")
    var filterOptionId: Int? = null,
    @SerializedName("filterType")
    var filterType: Int? = null,

    ) : Parcelable
