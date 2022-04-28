package com.selflearningcoursecreationapp.models.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserResponse(
    @SerializedName("expiration")
    var expiration: Long? = null,
    @SerializedName("token")
    var token: String? = null,
    @SerializedName("user", alternate = ["tokenResponse"])
    var user: UserProfile? = null
) : Parcelable