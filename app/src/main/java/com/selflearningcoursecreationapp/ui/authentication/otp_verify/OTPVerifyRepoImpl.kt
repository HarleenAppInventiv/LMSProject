package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class OTPVerifyRepoImpl(private val apiService: ApiService) : OTPVerifyRepo {
    override suspend fun reqOtp(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.signOTPReq(map)
            }

        }.safeApiCall(ApiEndPoints.API_OTP_REQ).flowOn(Dispatchers.IO)
    }

    override suspend fun verOtp(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.signOTPVal(map)
            }

        }.safeApiCall(ApiEndPoints.API_OTP_VAL).flowOn(Dispatchers.IO)
    }

    override suspend fun reqEmailOtp(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.requestEmailOTP(map)
            }

        }.safeApiCall(ApiEndPoints.API_ADD_EMAIL).flowOn(Dispatchers.IO)
    }

    override suspend fun verEmailOtp(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.validateEmailOTP(map)
            }

        }.safeApiCall(ApiEndPoints.API_VERIFY_EMAIL).flowOn(Dispatchers.IO)
    }
}