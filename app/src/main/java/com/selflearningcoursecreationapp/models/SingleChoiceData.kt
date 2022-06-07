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
    @SerializedName(
        "value",
        alternate = ["professionName", "courseComplexityName", "courseTypeName", "title", "targetAudienceName", "keyword"]
    )
    var title: String? = null
) : Parcelable

@Parcelize
data class SingleClickResponse(
    @SerializedName(
        "allProfessions",
        alternate = ["courseComplexities", "courseTypes", "searchKeywords"]
    )
    var list: ArrayList<SingleChoiceData>? = null
) : Parcelable

