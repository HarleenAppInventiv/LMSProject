package com.selflearningcoursecreationapp.base

import android.util.Log
import androidx.annotation.MainThread
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.utils.isInternetAvailable
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
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
                if (response.isSuccessful && data != null) {
                    emit(Resource.Success(data, apiCode))
                } else {
                    Resource.Failure(true, apiCode, getError(response))

                }
            } catch (e: Exception) {
                val apiError = setApiError(true) ?: ApiError()
                apiError?.exception = e

                when (e) {
                    is SocketTimeoutException, is TimeoutException, is IOException -> {
                        Log.d("main", "")
                    }
                    else -> {
                        apiError?.message = e?.message ?: ""
                    }
                }

                emit(Resource.Failure(false, apiCode, apiError))
            }
        } else {
            emit(Resource.Failure(true, apiCode, setApiError(false) ?: ApiError()))
        }
    }
/*
fun <T> safeAtpiCall(
    apiCode: String = ApiEndPoints.NO_API,
    apiCall: suspend () -> Response<BaseResponse<T>>,
): Resource {
    var dataClass = BaseResponse<T>()
    dataClass.apiCode = apiCode
    if (isInternetAvailable()) {
        return withContext(Dispatchers.IO) {
            try {

                val response: Response<BaseResponse<T>> = apiCall.invoke()
                if (response.body() != null) {
                    return@withContext if (response.isSuccessful && (response.body()!!.statusCode == 200 ||
                                response.body()!!.statusCode == 202 ||
                                response.body()!!.statusCode == 201)
                    ) {


                        dataClass = response.body()!!
                        //                        dataClass.apiError = null
                        Resource.Success(dataClass, apiCode)
                    } else {
                        Resource.Failure(true, apiCode, getError(response))

                    }
                } else {
                    return@withContext Resource.Failure(true, apiCode, getError(response))

                }
            } catch (e: Exception) {
                val apiError = setApiError(true) ?: ApiError()
                apiError?.exception = e

                when (e) {
                    is SocketTimeoutException, is TimeoutException, is IOException -> {
                    }
                    else -> {
                        apiError?.message = e?.message ?: ""
                    }
                }

                Resource.Failure(false, apiCode, apiError)

            }
        }
    } else {

        return Resource.Failure(true, apiCode, setApiError(false) ?: ApiError())

    }
}*/

    private fun <T> getError(response: Response<BaseResponse<T>>): ApiError {
        val error = ApiError()
        if (response.body() != null) {
            error.message = response.body()!!.message
            error.statusCode = response.body()!!.statusCode
            error.result = response.body()!!.data
        } else {
            val jObjError = JSONObject(response.errorBody()!!.string())
            //error = ErrorUtils.parseError(response)!!
            error.statusCode = jObjError.optInt("status_code")
            error.message = jObjError.optString("message")
                ?: "Unable to process your request. Please try again."
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

}