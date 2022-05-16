package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MasterResponse(
    @SerializedName("categories", alternate = ["allThemes"])
    var list: ArrayList<CategoryData>? = null,
) : Parcelable