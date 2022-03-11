package com.selflearningcoursecreationapp.data.network

open class ApiError {
    var status: Boolean = false
    var message: String? = null
    var result: Any? = null
    var statusCode: Int? = null
    var exception: Throwable? = null
}

data class ToastData(
    var errorCode: Int? = null,
    var errorString: String? = null
)
