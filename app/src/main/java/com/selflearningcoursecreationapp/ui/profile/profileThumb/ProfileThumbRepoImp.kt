package com.selflearningcoursecreationapp.ui.profile.profileThumb

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

class ProfileThumbRepoImp(private val apiService: ApiService) : ProfileThumbRepo {
    override suspend fun logout(): Flow<Resource> {
        return object : BaseRepo<Any>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.logoutUser()
            }

        }.safeApiCall(ApiEndPoints.API_LOGOUT).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAccount(): Flow<Resource> {
        return object : BaseRepo<UserResponse>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.deleteAccount()
            }

        }.safeApiCall(ApiEndPoints.API_DELETE_ACCOUNT).flowOn(Dispatchers.IO)
    }


}