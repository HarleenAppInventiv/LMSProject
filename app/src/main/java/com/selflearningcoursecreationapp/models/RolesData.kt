package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RolesData(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("roleDescription")
    var roleDescription: String? = null,
    @SerializedName("roleName")
    var roleName: String? = null
) : Parcelable