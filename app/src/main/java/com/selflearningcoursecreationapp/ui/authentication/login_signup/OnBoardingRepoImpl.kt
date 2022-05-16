package com.selflearningcoursecreationapp.ui.authentication.login_signup

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.AllProfessions
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class OnBoardingRepoImpl(private val apiService: ApiService) : OnBoardingRepo {
    override suspend fun signUpApi(map: UserProfile): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.signUp(map)
            }
        }.safeApiCall(ApiEndPoints.API_SIGNUP).flowOn(Dispatchers.IO)
    }

    override suspend fun loginInApi(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.loginUser(map)

            }
        }.safeApiCall(ApiEndPoints.API_LOGIN).flowOn(Dispatchers.IO)
    }

    override suspend fun professionApi(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AllProfessions>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AllProfessions>> {
                return apiService.profession()
            }

        }.safeApiCall(ApiEndPoints.API_PROFESSION).flowOn(Dispatchers.IO)

    }
}