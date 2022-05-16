package com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ChangePassRepoImp(private val apiService: ApiService) : ChangePassRepo {
    override suspend fun changePass(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.changePass(map)
            }

        }.safeApiCall(ApiEndPoints.API_CHANGE_PASS).flowOn(Dispatchers.IO)
    }
}