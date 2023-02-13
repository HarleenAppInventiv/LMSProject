package com.selflearningcoursecreationapp.ui.bottom_more

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class MoreFragmentRepoImp(private val apiService: ApiService) : MoreFragmentRepo {
    override suspend fun switchMod(userMode: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.switchToMod(userMode)
            }

        }.safeApiCall(ApiEndPoints.API_SWITCH_TO_MOD).flowOn(Dispatchers.IO)
    }

    override suspend fun support(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.postSupport(map)
            }

        }.safeApiCall(ApiEndPoints.API_SUPPORT).flowOn(Dispatchers.IO)
    }

//    override suspend fun staticPages(type: Int): Flow<Resource> {
//        return object : BaseRepo<BaseResponse<StaticUrlModel>>() {
//            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<StaticUrlModel>> {
//                return apiService.static(type)
//            }
//        }.safeApiCall(ApiEndPoints.API_STATIC).flowOn(Dispatchers.IO)
//    }
//
//    override suspend fun staticPagesFaq(): Flow<Resource> {
//        return object : BaseRepo<BaseResponse<StaticUrlModel>>() {
//            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<StaticUrlModel>> {
//                return apiService.static_faq()
//            }
//        }.safeApiCall(ApiEndPoints.API_STATIC_FAQ).flowOn(Dispatchers.IO)
//    }
}