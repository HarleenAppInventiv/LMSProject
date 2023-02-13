package com.selflearningcoursecreationapp.ui.notification

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.models.NotificationModel
import com.selflearningcoursecreationapp.ui.moderator.myCategories.ModeratorMyCategories
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class NotificationRepoImp(private val apiService: ApiService) : NotificationRepo {
    override suspend fun getNotification(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationModel>> {
                return apiService.getNotification()
            }
        }.safeApiCall(ApiEndPoints.API_GET_NOTIFICATION).flowOn(Dispatchers.IO)
    }

    override suspend fun patchNotification(notificationId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationData>> {
                return apiService.patchNotification(notificationId)
            }
        }.safeApiCall(ApiEndPoints.API_PATCH_NOTIFICATION).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteNotification(notificationId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationData>> {
                return apiService.deleteNotification(notificationId)
            }
        }.safeApiCall(ApiEndPoints.API_DELETE_NOTIFICATION).flowOn(Dispatchers.IO)
    }

    override suspend fun readAllNotification(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationData>> {
                return apiService.readAllNotification()
            }

        }.safeApiCall(ApiEndPoints.API_NOTIFICATION_READ_ALL).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAllNotification(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationData>> {
                return apiService.deleteAllNotification()
            }

        }.safeApiCall(ApiEndPoints.API_NOTIFICATION_DELETE_ALL).flowOn(Dispatchers.IO)
    }

    override suspend fun myCategories(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModeratorMyCategories>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModeratorMyCategories>> {
                return apiService.modMyCategories()
            }
        }.safeApiCall(ApiEndPoints.API_MOD_MY_CATEGORIES).flowOn(Dispatchers.IO)
    }
}