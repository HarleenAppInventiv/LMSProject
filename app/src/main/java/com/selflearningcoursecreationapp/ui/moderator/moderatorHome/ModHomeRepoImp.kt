package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.moderator.ModeratorDataSource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ModHomeRepoImp(private val apiService: ApiService) : ModHomeRepo {
    override suspend fun courseRequest(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ModeratorDataSource(apiService, data, type = "course_req")
            }
        ).liveData
    }

    override suspend fun modCourse(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ModeratorDataSource(apiService, data, type = "mod_Course")
            }
        ).liveData
    }

    override suspend fun updateCourseRequest(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.updateCourseRequest(map)
            }
        }.safeApiCall(ApiEndPoints.API_UPDATE_MOD_REQUEST).flowOn(Dispatchers.IO)
    }

    override suspend fun updateCourseStatus(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.updateCourseStatus(map)
            }
        }.safeApiCall(ApiEndPoints.API_UPDATE_MOD_COURSE_STATUS).flowOn(Dispatchers.IO)
    }

    override suspend fun getNotificationCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationData>> {
                return apiService.getNotificationCount()
            }
        }.safeApiCall(ApiEndPoints.API_NOTIFICATION_COUNT).flowOn(Dispatchers.IO)
    }
}