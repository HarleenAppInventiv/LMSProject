package com.selflearningcoursecreationapp.ui.profile.profileDetails

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ProfileDetailRepoImp(private val apiService: ApiService) : ProfileDetailRepo {
    override suspend fun viewProfile(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.viewProfile()
            }
        }.safeApiCall(ApiEndPoints.API_VIEW_PROFILE).flowOn(Dispatchers.IO)
    }

    override suspend fun uploadImage(
        body: MultipartBody.Part,
        fullName: RequestBody
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return apiService.uploadImage(body, fullName)
            }
        }.safeApiCall(ApiEndPoints.API_UPLOAD_IMAGE).flowOn(Dispatchers.IO)
    }
}