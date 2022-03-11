package com.selflearningcoursecreationapp.data.network

import ccom.example.roomwithmvvmdemo.all_states.StateData
import ccom.example.roomwithmvvmdemo.all_states.TestStateData
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.models.login.Data
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("all_counties")
    suspend fun getAllStates(): Response<BaseResponse<StateData>>

    @GET("all_counties")
    suspend fun allTestStates(): Response<BaseResponse<TestStateData>>

    @Headers("Authorization : Basic cmNjOnJjY0AxMjM=",
        "api_key:1234",
        "timezone:19800000",
        "language:en",
        "Access-Control-Allow-Origin:*",
        "Content-Type: application/json")
    @POST(ApiEndPoints.API_LOGIN)
    suspend fun login(@Body body: Any): Response<BaseResponse<Data>>

}
