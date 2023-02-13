package com.selflearningcoursecreationapp.ui.profile.profileThumb

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ProfileThumbRepoImp(private val apiService: ApiService) : ProfileThumbRepo {
    override suspend fun logout(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.logoutUser()
            }

        }.safeApiCall(ApiEndPoints.API_LOGOUT).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAccount(
        deleteUsers: Boolean,
        deleteWallet: Boolean
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.deleteAccount(deleteUsers, deleteWallet)
            }
        }.safeApiCall(ApiEndPoints.API_DELETE_ACCOUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun switchMod(userMode: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.switchToMod(userMode)
            }
        }.safeApiCall(ApiEndPoints.API_SWITCH_TO_MOD).flowOn(Dispatchers.IO)
    }

    override suspend fun changeViMode(changeViMode: ChangeViModeModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.changeViModeStatus(changeViMode)
            }
        }.safeApiCall(ApiEndPoints.API_CHANGE_VI_MODE).flowOn(Dispatchers.IO)
    }

    override suspend fun profileApi(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.viewProfile()
            }

        }.safeApiCall(ApiEndPoints.API_VIEW_PROFILE).flowOn(Dispatchers.IO)
    }

}