package com.selflearningcoursecreationapp.ui.search

import com.google.gson.annotations.SerializedName

data class ElasticSearchData(
    @SerializedName("list") val list: ArrayList<SearchList>? = null,
    @SerializedName("totalCount") val totalCount: Int? = 0,
    @SerializedName("pageNumber") val pageNumber: Int? = 0,
    @SerializedName("pageSize") val pageSize: Int? = 0,
    @SerializedName("resultCount") val resultCount: Int? = 0
)

data class SearchList(

    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("fee") val fee: String,
    @SerializedName("durations") val durations: Long,
    @SerializedName("totalSections") val totalSections: String,
    @SerializedName("totalLectures") val totalLectures: String,
    @SerializedName("averageRating") val averageRating: String,
    @SerializedName("totalReviews") val totalReviews: String,
    @SerializedName("creator") val creator: Creator,
    @SerializedName("category") val category: Category,
    @SerializedName("language") val language: Language,
    @SerializedName("complexityType") val complexityType: ComplexityType,
    @SerializedName("courseType") val courseType: CourseType,
    @SerializedName("logo") val logo: Logo,
    @SerializedName("banner") val banner: Banner,
    @SerializedName("rewardPoints") val rewardPoints: String
)


data class Logo(

    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("blurHash") val blurHash: String
)

data class Language(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)


data class Banner(

    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("blurHash") val blurHash: String
)

data class Category(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class ComplexityType(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class CourseType(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class Creator(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)