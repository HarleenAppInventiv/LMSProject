package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorAudienceStateCount
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCount
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCountWithFilter
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorTotalEarnings
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class CreatorDashboardRepoImp(private val apiService: ApiService) : CreatorDashboardRepo {

    override suspend fun audienceStatCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CreatorAudienceStateCount>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CreatorAudienceStateCount>> {
                return apiService.audiencedStatCount()
            }
        }.safeApiCall(ApiEndPoints.API_CREATOR_DASHBOARD_AUDIENCE_STAT_COUNT).flowOn(Dispatchers.IO)
    }


    override suspend fun creatorDashTotalEarning(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CreatorTotalEarnings>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CreatorTotalEarnings>> {
                return apiService.creatorDashTotalEarning(map)
            }
        }.safeApiCall(ApiEndPoints.API_CREATOR_DASHBOARD_TOTAL_EARNING).flowOn(Dispatchers.IO)
    }

    override suspend fun filteredCourseUserCount(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CreatorCourseUserCountWithFilter>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CreatorCourseUserCountWithFilter>> {
                return apiService.userCourseCountWithFilterCreatorDash(map)
            }
        }.safeApiCall(ApiEndPoints.API_CREATOR_DASHBOARD_COURSE_USER_COUNT_FILTERED)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun courseUserCount(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CreatorCourseUserCount>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CreatorCourseUserCount>> {
                return apiService.userCourseCountCreatorDash(map)
            }
        }.safeApiCall(ApiEndPoints.API_CREATOR_DASHBOARD_COURSE_USER_COUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun getMasterData(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<MasterDataItem>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<MasterDataItem>> {
                return apiService.getMasterData()
            }
        }.safeApiCall(ApiEndPoints.API_MASTER_DATA).flowOn(Dispatchers.IO)
    }

}