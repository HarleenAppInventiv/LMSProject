package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
@Parcelize
data class CategoryData(
    @SerializedName(
        "categoryName",
        alternate = ["fontName", "languageName", "themeName", "professionName", "category"]
    )
    var name: String? = null,

    @SerializedName("languageCode", alternate = ["fontCode", "themeCode", "themeID"])
    var code: String? = null,

    @SerializedName("image_Url")
    var imageUrl: String? = null,

    @SerializedName("isFaded")
    var isFaded: Boolean = false,

    @SerializedName("courseLogoBlurHash")
    var courseLogoBlurHash: String? = null,
    @Transient
    var imageId: Int? = null,
    @SerializedName("id", alternate = ["categoryId", "languageId"])
    var id: Int? = null,

    @SerializedName("status")
    var status: Int? = null,

    var codeId: Int? = null,

    var isSelected: Boolean = false,
    @SerializedName("minAgeFilter")
    var minAgeFilter: String? = null,

    @SerializedName("maxAgeFilter")
    var maxAgeFilter: String? = null,


    ) : Parcelable


@Parcelize
data class CategoryResponse(
    @SerializedName(
        "categories",
        alternate = ["allThemes", "languages", "myCategories", "categoryReponseLists"]
    )
    var list: ArrayList<CategoryData>? = null
) : Parcelable

@Parcelize
data class HomeAllCategoryResponse(
    @SerializedName("otherCategory")
    var otherCategories: ArrayList<CategoryData>? = null,
    @SerializedName("preferredCategory")
    var preferredCategories: ArrayList<CategoryData>? = null,
) : Parcelable