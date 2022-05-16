package com.selflearningcoursecreationapp.ui.authentication.resetPassword

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ResetPassRepoImpl(private val apiService: ApiService) : ResetPassRepo {
    override fun resetPass(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.resetPass(map)
            }

        }.safeApiCall(ApiEndPoints.API_RESET_PASS).flowOn(Dispatchers.IO)
    }
}