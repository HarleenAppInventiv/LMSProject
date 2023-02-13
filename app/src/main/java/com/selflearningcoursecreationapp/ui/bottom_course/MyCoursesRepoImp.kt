package com.selflearningcoursecreationapp.ui.bottom_course

import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.AllCoursesResponse
import com.selflearningcoursecreationapp.models.user.UserProfile
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

    override suspend fun editCourseToDraft(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.editToDraft(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_EDIT_TO_DRAFT,
                    courseId
                )
            }
        }.safeApiCall(ApiEndPoints.API_EDIT_TO_DRAFT).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteCourse(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.deleteCourse(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
                    courseId
                )
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_1).flowOn(Dispatchers.IO)
    }
}