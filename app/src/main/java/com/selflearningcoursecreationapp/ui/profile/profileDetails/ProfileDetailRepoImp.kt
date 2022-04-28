package com.selflearningcoursecreationapp.ui.profile.profileDetails

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ProfileDetailRepoImp(private val apiService: ApiService) : ProfileDetailRepo {
    override suspend fun viewProfile(): Flow<Resource> {
        return object : BaseRepo<UserProfile>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.viewProfile()
            }
        }.safeApiCall(ApiEndPoints.API_VIEW_PROFILE).flowOn(Dispatchers.IO)
    }
}