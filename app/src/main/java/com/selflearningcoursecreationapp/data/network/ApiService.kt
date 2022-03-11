package com.selflearningcoursecreationapp.data.network

import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.models.StateData
import com.selflearningcoursecreationapp.models.TestStateData
import com.selflearningcoursecreationapp.models.login.Data
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @GET("all_counties")
    suspend fun getAllStates(): Response<BaseResponse<StateData>>

    @GET("all_counties")
    suspend fun allTestStates(): Response<BaseResponse<TestStateData>>

    @Headers(
        "Authorization : Basic cmNjOnJjY0AxMjM=",
        "api_key:1234",
        "timezone:19800000",
        "language:en",
        "Access-Control-Allow-Origin:*",
        "Content-Type: application/json"
    )
    @POST(ApiEndPoints.API_LOGIN)
    suspend fun login(@Body body: Any): Response<BaseResponse<Data>>

}
