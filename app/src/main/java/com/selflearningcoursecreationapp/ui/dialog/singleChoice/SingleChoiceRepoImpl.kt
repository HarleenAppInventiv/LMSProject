package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.SingleClickResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class SingleChoiceRepoImpl(private val apiService: ApiService) : SingleChoiceRepo {


    override suspend fun professionApi(): Flow<Resource> {
        return object : BaseRepo<SingleClickResponse>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<SingleClickResponse>> {
                return apiService.getProfession()
            }

        }.safeApiCall(ApiEndPoints.API_PROFESSION).flowOn(Dispatchers.IO)

    }
}