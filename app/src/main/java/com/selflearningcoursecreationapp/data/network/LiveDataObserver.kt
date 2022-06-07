package com.selflearningcoursecreationapp.data.network

import androidx.lifecycle.Observer
import com.selflearningcoursecreationapp.extensions.showLog

interface LiveDataObserver : Observer<EventObserver<Resource>> {
    fun <T> onResponseSuccess(value: T, apiCode: String)
    fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String)
    fun onError(error: ToastData, apiCode: String?)
    fun onLoading(message: String, apiCode: String?)
    override fun onChanged(event: EventObserver<Resource>) {
        showLog("API_RESPONSE", "first response")

        event.getContentIfNotHandled()?.let { apiResponse ->
            showLog("API_RESPONSE", "handled response $apiResponse")
            when (apiResponse) {
                is Resource.Success<*> -> {
                    onResponseSuccess(apiResponse.value, apiResponse.apiCode)
                }
                is Resource.Failure -> {
                    onException(
                        apiResponse.isNetworkError,
                        apiResponse.exception,
                        apiResponse.apiCode
                    )
                }
                is Resource.Error -> {
                    onError(apiResponse.error, apiResponse.apiCode)
                }
                is Resource.Loading -> {
                    onLoading(apiResponse.message ?: "", apiResponse.apiCode)
                }
            }
        }

    }
}