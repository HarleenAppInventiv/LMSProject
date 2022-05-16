package com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture

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

class DocRepoImp(private val apiService: ApiService) : DocRepo {
    override suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.addPatchLecture(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_LECTURE_PATCH).flowOn(Dispatchers.IO)
    }
}