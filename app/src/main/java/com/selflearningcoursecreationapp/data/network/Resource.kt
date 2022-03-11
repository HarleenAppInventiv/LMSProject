package com.selflearningcoursecreationapp.data.network

sealed class Resource {
    data class Success<T>(val value: T, val apiCode: String) : Resource()
    data class Failure(val isNetworkError: Boolean, val apiCode: String, val exception: ApiError) :
        Resource()

    data class Error( val error: ToastData) : Resource()
    data class Loading(val message: String? = "") : Resource()


}

