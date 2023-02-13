package com.selflearningcoursecreationapp.data.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ApiError : Parcelable {
    var status: Boolean = false
    var message: String? = null
    var result: Any? = null
    var data: Any? = null
    var statusCode: Int? = null
    var exception: Throwable? = null
}

data class ToastData(
    var errorCode: Int? = null,
    var errorString: String? = null
)
