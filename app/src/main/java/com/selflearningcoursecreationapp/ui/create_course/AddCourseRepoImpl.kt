package com.selflearningcoursecreationapp.ui.create_course

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class AddCourseRepoImpl(private val apiService: ApiService) : AddCourseRepo {
    override suspend fun step1AddCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.addCourseStep1(map)
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_1).flowOn(Dispatchers.IO)
    }

    override suspend fun step1UpdateCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.updateCourseStep1(map)
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_1).flowOn(Dispatchers.IO)
    }

    override suspend fun step2AddCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.addCourseStep2(map)
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_2).flowOn(Dispatchers.IO)
    }

    override suspend fun getMasterData(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<MasterDataItem>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<MasterDataItem>> {
                return apiService.getMasterData()
            }
        }.safeApiCall(ApiEndPoints.API_MASTER_DATA).flowOn(Dispatchers.IO)
    }

    override suspend fun uploadCourseImage(
        filePart: MultipartBody.Part,
        body: RequestBody,
        courseId: RequestBody?,
        banner: Boolean,
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return if (banner) apiService.uploadCourseBanner(
                    filePart,
                    body,
                    courseId
                ) else apiService.uploadCourseLogo(filePart, body, courseId)
            }
        }.safeApiCall(ApiEndPoints.API_UPLOAD_IMAGE).flowOn(Dispatchers.IO)
    }

    override suspend fun addSection(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.addSection(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_SECTION_POST).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteSection(courseId: String, sectionId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.deleteSection(courseId, sectionId)
            }
        }.safeApiCall(ApiEndPoints.API_SECTION_DELETE).flowOn(Dispatchers.IO)
    }

    override suspend fun dragAndDropSection(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.dragAndDropSection(map)
            }
        }.safeApiCall(ApiEndPoints.API_SECTION_DRAG_DROP).flowOn(Dispatchers.IO)
    }

    override suspend fun addLecture(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.addLecture(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_LECTURE_POST).flowOn(Dispatchers.IO)
    }


    override suspend fun deleteLecture(
        courseId: String,
        sectionId: String,
        lectureId: String,
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.deleteLecture(courseId, sectionId, lectureId)
            }
        }.safeApiCall(ApiEndPoints.API_LECTURE_DELETE).flowOn(Dispatchers.IO)
    }


    override suspend fun dragAndDropLecture(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.dragAndDropLecture(map)
            }
        }.safeApiCall(ApiEndPoints.API_LECTURE_DRAG_DROP).flowOn(Dispatchers.IO)
    }

    override suspend fun addPatchSection(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.addPatchSection(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_SECTION_PATCH).flowOn(Dispatchers.IO)
    }


}