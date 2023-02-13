package com.selflearningcoursecreationapp.models


import com.google.gson.annotations.SerializedName

data class StaticUrlModel(
    @SerializedName("answer")
    val answer: String?,
    @SerializedName("displayOrder")
    val displayOrder: Int?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("questionTitle")
    val questionTitle: String?,
    @SerializedName("screenId")
    val screenId: Int?
)