package com.selflearningcoursecreationapp.ui.create_course.co_author

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class CoAuthorRepoImpl(private var apiService: ApiService) : CoAuthorRepo {
    override suspend fun inviteCoAuthor(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.inviteCoAuthor(map)
            }

        }.safeApiCall(ApiEndPoints.API_INVITE_COAUTHOR).flowOn(Dispatchers.IO)
    }

    override suspend fun existsCoAuthor(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ExistsCoAuthorResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ExistsCoAuthorResponse>> {
                return apiService.existsCoAuthorDetails(courseId)
            }

        }.safeApiCall(ApiEndPoints.API_EXISTS_COAUTHOR).flowOn(Dispatchers.IO)
    }

}