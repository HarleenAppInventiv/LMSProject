package com.selflearningcoursecreationapp.ui.course_details.ratings.model

import com.google.gson.annotations.SerializedName

data class GetReviewsRequestModel(

    @field:SerializedName("sortingField")
    var sortingField: SortingField? = null,

    @field:SerializedName("pageNumber")
    var pageNumber: Int? = null,

    @field:SerializedName("pageSize")
    var pageSize: Int? = null,

    @field:SerializedName("generalSearchField")
    val generalSearchField: String? = null,

    @field:SerializedName("searchFields")
    var searchFields: ArrayList<SearchFieldsItem>? = null,

    @field:SerializedName("courseId")
    var courseId: Int? = null,

    @field:SerializedName("courseType")
    var courseType: Int? = null
)

data class SearchFieldsItem(

    @field:SerializedName("fieldName")
    val fieldName: String? = null,

    @field:SerializedName("operatorType")
    val operatorType: Int? = null,

    @field:SerializedName("fieldValue")
    val fieldValue: ArrayList<String?>? = null
)

data class SortingField(

    @field:SerializedName("fieldName")
    var fieldName: String? = null,

    @field:SerializedName("direction")
    var direction: String? = null
)
