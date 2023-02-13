package com.selflearningcoursecreationapp.models.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PreferenceData(
    @SerializedName("categorySingleSelection")
    var categorySingleSelection: Boolean = false,
    @SerializedName("isSignIncluded")
    var isSignIncluded: Boolean = false,
    @SerializedName("currentSelection")
    var currentSelection: Int = -1,
    @SerializedName("languageSingleSelection")
    var languageSingleSelection: Boolean = true,
    @SerializedName("screenType")
    var screenType: Int = 1,
    @SerializedName("clickType")
    var clickType: Int = 1,
    @SerializedName("selectedValues")
    var selectedValues: List<Int?>? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("type")
    var type: Int = -1,
    @SerializedName("from")
    var from: Boolean = false

) : Parcelable