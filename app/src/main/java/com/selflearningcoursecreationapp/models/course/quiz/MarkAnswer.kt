package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarkAnswer(
    @SerializedName("answere1")
    var answere1: Int? = null,
    @SerializedName("answere2")
    var answere2: Int? = null
) : Parcelable