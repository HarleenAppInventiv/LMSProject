package com.selflearningcoursecreationapp.ui.bottom_course

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.AllCoursesResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class MyCoursesRepoImp(private val apiService: ApiService) : MyCoursesRepo {
    override suspend fun onGoing(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AllCoursesResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AllCoursesResponse>> {
                return apiService.getOnGoingCourses(map)
            }
        }.safeApiCall(ApiEndPoints.API_ALL_COURSES).flowOn(Dispatchers.IO)
    }
}