package com.selflearningcoursecreationapp.ui.profile.requestTracker

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.moderator.ModeratorDataSource
import com.selflearningcoursecreationapp.ui.moderator.model.RequestCountModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class RequestTrackerRepoImp(private val apiService: ApiService) : RequestTrackerRepo {
    override suspend fun modStatus(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.modStatus()
            }
        }.safeApiCall(ApiEndPoints.API_MOD_STATUS).flowOn(Dispatchers.IO)
    }

    override suspend fun requestCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<RequestCountModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<RequestCountModel>> {
                return apiService.requestCount()
            }
        }.safeApiCall(ApiEndPoints.API_REQUEST_COUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun getRequestList(datamap: HashMap<String, Int>): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ModeratorDataSource(apiService, datamap)
            }
        ).liveData
    }

    override suspend fun manageInvitation(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.manageCoAuthorInvitation(map)
            }

        }.safeApiCall(ApiEndPoints.API_COAUTHOR_INVITATION).flowOn(Dispatchers.IO)
    }
}