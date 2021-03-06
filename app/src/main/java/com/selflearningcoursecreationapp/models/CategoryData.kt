package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryData(
    @SerializedName(
        "categoryName",
        alternate = ["fontName", "languageName", "themeName", "professionName"]
    )
    var name: String? = null,
    @SerializedName("languageCode", alternate = ["fontCode", "themeCode", "themeID"])
    var code: String? = null,
    @SerializedName("image_Url")
    var imageUrl: String? = null,

    @SerializedName("id", alternate = ["categoryId", "languageId"])
    var id: Int? = null,
    var codeId: Int? = null,

    var isSelected: Boolean = false,
) : Parcelable


@Parcelize
data class CategoryResponse(
    @SerializedName("categories", alternate = ["allThemes", "languages", "myCategories"])
    var list: ArrayList<CategoryData>? = null,
) : Parcelable