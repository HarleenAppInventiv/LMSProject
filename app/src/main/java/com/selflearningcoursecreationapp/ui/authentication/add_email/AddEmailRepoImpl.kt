package com.selflearningcoursecreationapp.ui.authentication.add_email

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class AddEmailRepoImpl(var apiService: ApiService) : AddEmailRepo {

    override suspend fun addEmail(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<Any>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.addEmail(map)

            }
        }.safeApiCall(ApiEndPoints.API_ADD_EMAIL).flowOn(Dispatchers.IO)
    }
}