package com.selflearningcoursecreationapp.ui.moderator.model

import com.google.gson.annotations.SerializedName

data class AddModeratorResponse(

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("documentsContentId")
    val documentsContentId: List<String?>? = null,

    @field:SerializedName("categoryId")
    val category: String? = null
)
