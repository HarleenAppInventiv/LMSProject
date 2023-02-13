package com.selflearningcoursecreationapp.ui.create_course.co_author

import com.google.gson.annotations.SerializedName

data class ExistsCoAuthorResponse(

    @SerializedName("phone") val phone: String,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("email") val email: String,
    @SerializedName("coAuthorExists") val coAuthorExists: Boolean

)
