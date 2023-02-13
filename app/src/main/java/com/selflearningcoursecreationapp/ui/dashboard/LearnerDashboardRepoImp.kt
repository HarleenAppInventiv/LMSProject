package com.selflearningcoursecreationapp.ui.dashboard

import LearnerDashboardData
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.dashboard.model.ActivityHoursModel
import com.selflearningcoursecreationapp.ui.dashboard.model.LearnerDashboardFilteredStatCountModel
import com.selflearningcoursecreationapp.ui.dashboard.model.LearnerDashboardStatCountModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class LearnerDashboardRepoImp(private val apiService: ApiService) : LearnerDashboardRepo {

    override suspend fun courseStatCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<LearnerDashboardStatCountModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<LearnerDashboardStatCountModel>> {
                return apiService.learnerDashboardStatCount()
            }
        }.safeApiCall(ApiEndPoints.API_LEARNER_DASHBOARD_STAT_COUNT).flowOn(Dispatchers.IO)
    }


    override suspend fun getEnrolledCourses(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<LearnerDashboardData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<LearnerDashboardData>> {
                return apiService.getLearnerDashboardCoursesList(map)
            }
        }.safeApiCall(ApiEndPoints.API_LEARNER_DASHBOARD_COURSES_LIST).flowOn(Dispatchers.IO)
    }

    override suspend fun getFilteredCount(map: GetReviewsRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<LearnerDashboardFilteredStatCountModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<LearnerDashboardFilteredStatCountModel>> {
                return apiService.getFiltedLearnerDashboardCount(map)
            }
        }.safeApiCall(ApiEndPoints.API_LEARNER_DASHBOARD_FILTERED_COUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun getTrackUserActivity(startDate: String, endDate: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ActivityHoursModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ActivityHoursModel>> {
                return apiService.getActivityTimeTracer(startDate, endDate)
            }
        }.safeApiCall(ApiEndPoints.API_CREATOR_DASHBOARD_TOTAL_ACTIVITY_TIME).flowOn(Dispatchers.IO)
    }
}