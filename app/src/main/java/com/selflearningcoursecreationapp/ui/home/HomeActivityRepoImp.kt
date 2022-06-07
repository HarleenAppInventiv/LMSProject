package com.selflearningcoursecreationapp.ui.home

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

class HomeActivityRepoImp(private val apiService: ApiService) : HomeActivityRepo {
    override suspend fun manageCouthorInvitation(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.manageCoAuthorInvitation(map)
            }

        }.safeApiCall(ApiEndPoints.API_COAUTHOR_INVITATION + "/home").flowOn(Dispatchers.IO)
    }
}