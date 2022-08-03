package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class HomeFilterRepoImpl(private var apiService: ApiService) : HomeFilterRepo {
    override suspend fun getFilters(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<FilterResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<FilterResponse>> {
                return apiService.getHomeFilters()
            }
        }.safeApiCall(ApiEndPoints.API_HOME_FILTERS).flowOn(Dispatchers.IO)

    }

    override suspend fun getRatingFilters(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<FilterResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<FilterResponse>> {
                return apiService.getReviewFilters(courseId.toString())
            }
        }.safeApiCall(ApiEndPoints.API_REVIEW_FILTERS).flowOn(Dispatchers.IO)

    }
}