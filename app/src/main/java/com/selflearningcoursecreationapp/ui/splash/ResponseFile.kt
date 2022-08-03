package com.selflearningcoursecreationapp.ui.splash

import com.google.gson.annotations.SerializedName

data class ResponseFile(

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("cityname")
    val cityname: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("company")
    val company: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("designation")
    val designation: String? = null
)
