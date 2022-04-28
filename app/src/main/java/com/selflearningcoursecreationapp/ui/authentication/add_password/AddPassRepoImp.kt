package com.selflearningcoursecreationapp.ui.authentication.add_password

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

class AddPassRepoImp(var apiService: ApiService) : AddPassRepo {
    override suspend fun addPass(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<UserResponse>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.addPassword(map)

            }
        }.safeApiCall(ApiEndPoints.API_ADD_PASSWORD).flowOn(Dispatchers.IO)
    }
}