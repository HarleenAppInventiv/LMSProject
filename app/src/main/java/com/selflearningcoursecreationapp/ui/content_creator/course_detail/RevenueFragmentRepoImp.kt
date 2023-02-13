package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import RevenueDataModel
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class RevenueFragmentRepoImp(private val apiService: ApiService) : RevenueFragmentRepo {

    override suspend fun getRevenueList(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<RevenueDataModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<RevenueDataModel>> {
                return apiService.getRevenueList(map)
            }
        }.safeApiCall(ApiEndPoints.API_GET_REVENUE_LIST).flowOn(Dispatchers.IO)
    }

}