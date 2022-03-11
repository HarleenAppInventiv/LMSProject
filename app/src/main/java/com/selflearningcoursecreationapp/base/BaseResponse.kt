package com.selflearningcoursecreationapp.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseResponse<T> {
    var apiCode: String = ""
    var isInternetOn = true

    @SerializedName("statusCode")
    @Expose
    var statusCode: Int = 0
  @SerializedName("status")
    @Expose
    var status: String = ""

    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("data")
    @Expose
    var data: T? = null
}
