package com.selflearningcoursecreationapp.data.network

import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.models.*
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST(ApiEndPoints.API_SIGNUP)
    suspend fun signUp(@Body body: UserProfile): Response<BaseResponse<UserProfile>>

    @POST(ApiEndPoints.API_OTP_REQ)
    suspend fun signOTPReq(@Body body: Any): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.API_ADD_EMAIL)
    suspend fun requestEmailOTP(@Body body: Any): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_VERIFY_EMAIL)
    suspend fun validateEmailOTP(@Body body: Any): Response<BaseResponse<UserResponse>>


    @POST(ApiEndPoints.API_OTP_VAL)
    suspend fun signOTPVal(@Body body: Any): Response<BaseResponse<UserResponse>>


    @POST(ApiEndPoints.API_LOGIN)
    suspend fun loginUser(@Body body: Any): Response<BaseResponse<UserResponse>>

    @POST(ApiEndPoints.API_SAVE_PREFRENCE)
    suspend fun savePrefrence(@Body body: Any): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.API_GET_CATEGORIES)
    suspend fun getCategories(): Response<BaseResponse<CategoryResponse>>


    @GET(ApiEndPoints.API_MYCATEGORIES)
    suspend fun myCategories(@Body body: Any): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.API_GETTHEME_LIST)
    suspend fun getThemeList(): Response<BaseResponse<CategoryResponse>>


    @GET(ApiEndPoints.API_VIEW_PROFILE)
    suspend fun viewProfile(): Response<BaseResponse<UserProfile>>


    @POST(ApiEndPoints.API_CHANGE_PASS)
    suspend fun changePass(@Body body: Any): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_RESET_PASS)
    suspend fun resetPass(@Body body: Any): Response<BaseResponse<Any>>

    @PATCH(ApiEndPoints.API_UPDATE_PROFILE)
    suspend fun updateProfile(@Body body: Any): Response<BaseResponse<UserResponse>>

    @DELETE(ApiEndPoints.API_DELETE_ACCOUNT)
    suspend fun deleteAccount(): Response<BaseResponse<UserResponse>>

    @POST(ApiEndPoints.API_LOGOUT)
    suspend fun logoutUser(
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.API_PROFESSION)
    suspend fun profession(
    ): Response<BaseResponse<AllProfessions>>

    @GET(ApiEndPoints.API_PROFESSION)
    suspend fun getProfession(
    ): Response<BaseResponse<SingleClickResponse>>

    @GET(ApiEndPoints.API_GET_ALL_STATES)
    suspend fun stateList(
    ): Response<BaseResponse<ArrayList<StateModel>>>

    @GET(ApiEndPoints.API_GET_ALL_CITY)
    suspend fun cityList(@Query("StateId") stateId: Int): Response<BaseResponse<ArrayList<CityModel>>>

    @POST(ApiEndPoints.API_ADD_PASSWORD)
    suspend fun addPassword(@Body body: Any): Response<BaseResponse<UserResponse>>

    @POST(ApiEndPoints.API_ADD_EMAIL)
    suspend fun addEmail(@Body body: Any): Response<BaseResponse<Any>>
}
