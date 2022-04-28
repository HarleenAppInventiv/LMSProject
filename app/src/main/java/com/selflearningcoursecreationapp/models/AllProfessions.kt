package com.selflearningcoursecreationapp.models

import android.os.Parcelable


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllProfessions(
    @SerializedName("allProfessions")
    val list: ArrayList<ProfessionModel>
) : Parcelable

@Parcelize
data class ProfessionModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("is_selected") var isSelected: Boolean? = false,
    @SerializedName("professionName") var professionName: String? = null,
) : Parcelable