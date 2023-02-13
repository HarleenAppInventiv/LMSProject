package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import ModDashboardData
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.moderator.model.ModDashboardStatCountModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ModDashboardRepoImp(private val apiService: ApiService) : ModDashboardRepo {

    override suspend fun courseStatCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModDashboardStatCountModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModDashboardStatCountModel>> {
                return apiService.modDashboardStatCount()
            }
        }.safeApiCall(ApiEndPoints.API_MOD_DASHBOARD_STAT_COUNT).flowOn(Dispatchers.IO)
    }


    override suspend fun acceptedCourses(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModDashboardData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModDashboardData>> {
                return apiService.getModDashboardCoursesList(map)
            }
        }.safeApiCall(ApiEndPoints.API_MOD_DASHBOARD_COURSES_LIST).flowOn(Dispatchers.IO)
    }

    override suspend fun getFilteredCount(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModDashboardStatCountModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModDashboardStatCountModel>> {
                return apiService.getFiltedModDashboardCount(map)
            }
        }.safeApiCall(ApiEndPoints.API_MOD_DASHBOARD_FILTERED_COUNT).flowOn(Dispatchers.IO)
    }

}