package com.selflearningcoursecreationapp.base

import android.util.Log
import androidx.annotation.MainThread
import com.google.gson.Gson
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.isInternetAvailable
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

abstract class BaseRepo<REQUEST> {

    @MainThread
    protected abstract suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<REQUEST>>


    fun safeApiCall(apiCode: String) = flow {
        if (isInternetAvailable()) {
            emit(Resource.Loading())
            try {
                val response = fetchDataFromRemoteSource()
                val data = response.body()
                when {
                    response.isSuccessful -> {

                        handleSuccessResponse(data, apiCode, response)

                    }
                    else -> {
                        val error = Gson().fromJson(
                            JSONObject(
                                JSONObject(
                                    response.errorBody()?.string()
                                        ?: "{}"
                                ).toString()
                            ).toString(), ApiError::class.java
                        )
                        emit(Resource.Failure(false, apiCode, error))
                    }
                }
            } catch (e: Exception) {
                val error = handleException(e)
                emit(Resource.Failure(false, apiCode, error))
            }
        } else {
            emit(Resource.Failure(true, apiCode, setApiError(false) ?: ApiError()))
        }
    }

    private suspend fun FlowCollector<Resource>.handleSuccessResponse(
        data: BaseResponse<REQUEST>?,
        apiCode: String,
        response: Response<BaseResponse<REQUEST>>
    ) {
        if (data != null && (data.statusCode == HTTPCode.SUCCESS || data.statusCode == HTTPCode.CREATED || data.statusCode == HTTPCode.AUTOMATIC_COMPLETE || data.statusCode == HTTPCode.UNJOIN || data.statusCode == HTTPCode.UPDATE_SUCCESS)) emit(
            Resource.Success(
                data,
                apiCode
            )
        ) else emit(Resource.Failure(false, apiCode, getError(response)))
    }

    private fun getError(response: Response<BaseResponse<REQUEST>>): ApiError {
        var error = ApiError()
        if (response.body() != null) {
            error.message = response.body()!!.message
            error.statusCode = response.body()!!.statusCode
            error.result = response.body()!!.resource
        } else {
            error = Gson().fromJson(response.errorBody()?.string(), ApiError::class.java)
        }
        return error
    }


    private fun setApiError(isInternetOn: Boolean): ApiError? {
        val apiError = ApiError()
        if (isInternetOn)
            apiError.message = "Request failed. Please retry"
        else
            apiError.message =
                "It seems like you are not connected with a stable internet connection"
        return apiError

    }

    fun handleException(e: Exception): ApiError {
        val apiError = setApiError(true) ?: ApiError()
        apiError.exception = e
        when (e) {
            is SocketTimeoutException, is TimeoutException, is IOException -> {
                Log.d("main", "")
            }
            else -> {
                apiError.message = e.message ?: ""
            }
        }
        return apiError
    }

}