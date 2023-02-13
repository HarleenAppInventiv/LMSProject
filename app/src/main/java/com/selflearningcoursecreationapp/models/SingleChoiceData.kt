package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SingleChoiceData(
    @SerializedName("id", alternate = ["courseComplexityId", "courseTypeId"])
    var id: Int? = null,

    @SerializedName("isSelected")
    var isSelected: Boolean? = null,

    @SerializedName("isPaid")
    var isPaid: Boolean? = null,


    @SerializedName("isFaded")
    var isFaded: Boolean = false,

    @SerializedName(
        "value",
        alternate = ["professionName", "courseComplexityName", "courseTypeName", "title", "targetAudienceName", "keyword"]
    )
    var title: String? = null,
    @Transient
    var isEnabled: Boolean = true
) : Parcelable

@Parcelize
data class SingleClickResponse(
    @SerializedName(
        "allProfessions",
        alternate = ["courseComplexities", "courseTypes", "searchKeywords"]
    )
    var list: ArrayList<SingleChoiceData>? = null
) : Parcelable

