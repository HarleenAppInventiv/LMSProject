package com.selflearningcoursecreationapp.ui.course_details.ratings.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetReviewsRequestModel(

    @field:SerializedName("sortingField")
    var sortingField: SortingField? = null,

    @field:SerializedName("pageNumber")
    var pageNumber: Int? = null,
    @field:SerializedName("status")
    var status: Int? = null,

    @field:SerializedName("RequestType")
    var RequestType: Int? = null,

    @field:SerializedName("pageSize")
    var pageSize: Int? = null,

    @field:SerializedName("generalSearchField")
    var generalSearchField: String? = null,

    @field:SerializedName("searchFields")
    var searchFields: ArrayList<SearchFieldsItem>? = null,

    @field:SerializedName("courseId")
    var courseId: Int? = null,

    @field:SerializedName("reportReviewAlready")
    var reportReviewAlready: Boolean? = null,

    @field:SerializedName("reviewId")
    var reviewId: Int? = null,

    @field:SerializedName("courseType")
    var courseType: Int? = null,

    @field:SerializedName("moderatorStatus")
    var moderatorStatus: Int? = null,

    @field:SerializedName("transactionTypeId")
    var transactionTypeId: Int? = null

) : Parcelable

@Parcelize
data class SearchFieldsItem(

    @field:SerializedName("fieldName")
    var fieldName: String? = null,

    @field:SerializedName("operatorType")
    val operatorType: Int? = null,

    @field:SerializedName("fieldValue")
    val fieldValue: ArrayList<String?>? = null
) : Parcelable

@Parcelize
data class SortingField(

    @field:SerializedName("fieldName")
    var fieldName: String? = null,

    @field:SerializedName("direction")
    var direction: String? = null
) : Parcelable
