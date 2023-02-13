package com.selflearningcoursecreationapp.ui.home

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.moderator.myCategories.ModeratorMyCategories
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class HomeActivityRepoImp(private val apiService: ApiService) : HomeActivityRepo {
    override suspend fun manageCouthorInvitation(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.manageCoAuthorInvitation(map)
            }

        }.safeApiCall(ApiEndPoints.API_COAUTHOR_INVITATION + "/home").flowOn(Dispatchers.IO)
    }

    override suspend fun purchaseCourse(map: HashMap<String, Int>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.purchaseCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_PURCHASE_COURSE).flowOn(Dispatchers.IO)
    }

    override suspend fun switchMod(userMode: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.switchToMod(userMode)
            }
        }.safeApiCall(ApiEndPoints.API_SWITCH_TO_MOD).flowOn(Dispatchers.IO)
    }

    override suspend fun patchNotification(notificationId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<NotificationData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<NotificationData>> {
                return apiService.patchNotification(notificationId)
            }
        }.safeApiCall(ApiEndPoints.API_PATCH_NOTIFICATION).flowOn(Dispatchers.IO)
    }

    override suspend fun profileApi(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.viewProfile()
            }

        }.safeApiCall(ApiEndPoints.API_VIEW_PROFILE).flowOn(Dispatchers.IO)
    }

    override suspend fun courseStatusPolicy(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.coursePolicyStatus(map)
            }
        }.safeApiCall(ApiEndPoints.API_COURSE_POLICY_STATUS).flowOn(Dispatchers.IO)
    }

    override suspend fun myCategories(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModeratorMyCategories>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModeratorMyCategories>> {
                return apiService.modMyCategories()
            }
        }.safeApiCall(ApiEndPoints.API_MOD_MY_CATEGORIES).flowOn(Dispatchers.IO)
    }

}