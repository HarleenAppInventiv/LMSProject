package com.selflearningcoursecreationapp.ui.moderator.myCategories

import com.google.gson.annotations.SerializedName

data class ModeratorMyCategories(

    @SerializedName("languages") val languages: List<Languages>,
    @SerializedName("categories") val categories: List<Categories>
)

data class Categories(

    @SerializedName("status") val status: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("imageURL") val imageURL: String
)

data class Languages(

    @SerializedName("languageId") val languageId: Int,
    @SerializedName("languageName") val languageName: String,
    @SerializedName("languageCode") val languageCode: String,
    @SerializedName("type") val type: Int
)