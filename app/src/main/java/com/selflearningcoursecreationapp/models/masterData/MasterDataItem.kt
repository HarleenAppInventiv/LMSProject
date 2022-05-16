package com.selflearningcoursecreationapp.models.masterData


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.models.AllProfessions
import com.selflearningcoursecreationapp.models.CategoryResponse
import com.selflearningcoursecreationapp.models.SingleClickResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MasterDataItem(
    @SerializedName("categories")
    var categories: CategoryResponse? = null,
    @SerializedName("courseComplexities")
    var courseComplexities: SingleClickResponse? = null,
    @SerializedName("courseTypes")
    var courseTypes: SingleClickResponse? = null,
    @SerializedName("languages")
    var languages: CategoryResponse? = null,
    @SerializedName("professions")
    var professions: AllProfessions? = null
) : Parcelable