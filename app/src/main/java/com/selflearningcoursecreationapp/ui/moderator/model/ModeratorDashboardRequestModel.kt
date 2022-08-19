package com.selflearningcoursecreationapp.ui.moderator.model

import com.google.gson.annotations.SerializedName

data class ModeratorDashboardRequestModel(


    @field:SerializedName("pageNumber")
    var pageNumber: Int? = null,

    @field:SerializedName("pageSize")
    var pageSize: Int? = null,

    @field:SerializedName("RequestType")
    var RequestType: Int? = null,


    )


