package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StateModel(
    @SerializedName("countryId")
    var countryId: Int? = null,
    @SerializedName("countryName")
    var countryName: String? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = null,
    @SerializedName("stateId")
    var stateId: Int? = null,
    @SerializedName("stateName")
    var stateName: String? = null,
) : Parcelable

@Parcelize
data class CityModel(
    @SerializedName("cityId")
    var cityId: Int? = null,
    @SerializedName("cityName")
    var cityName: String? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = null,
    @SerializedName("stateId")
    var stateId: Int? = null,
    @SerializedName("stateName")
    var stateName: String? = null,
) : Parcelable