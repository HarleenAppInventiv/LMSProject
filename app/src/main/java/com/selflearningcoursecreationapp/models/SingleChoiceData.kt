package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SingleChoiceData(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("isSelected")
    var isSelected: Boolean? = null,
    @SerializedName("title", alternate = ["professionName"])
    var title: String? = null
) : Parcelable

@Parcelize
data class SingleClickResponse(
    @SerializedName("allProfessions")
    var list: ArrayList<SingleChoiceData>? = null
) : Parcelable

