package com.selflearningcoursecreationapp.ui.create_course.upload_content

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.getMultiPartBody
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.File

class UploadContentRepoImpl(private val apiService: ApiService) : UploadContentRepo {
    override suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.addPatchLecture(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_LECTURE_PATCH).flowOn(Dispatchers.IO)
    }

    override suspend fun getLectureDetail(lectureId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.getLectureDetail(lectureId)
            }
        }.safeApiCall(ApiEndPoints.API_GET_LECTURE_DETAIL).flowOn(Dispatchers.IO)
    }

    override suspend fun contentUpload(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        file: File,
        uploadType: Int,
        text: String,
        duration: Int
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return apiService.contentUpload(
                    courseId.getRequestBody(),
                    sectionId.getRequestBody(),
                    lectureId.getRequestBody(),
                    file.name.getRequestBody(),
                    file.getMultiPartBody("File"),
                    uploadType.getRequestBody(),
                    text.getRequestBody(),
                    duration.getRequestBody()

                )
            }
        }.safeApiCall(ApiEndPoints.API_CONTENT_UPLOAD).flowOn(Dispatchers.IO)
    }

    override suspend fun thumbnailUpload(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        file: File
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return apiService.thumbnailUpload(
                    courseId.getRequestBody(),
                    sectionId.getRequestBody(),
                    lectureId.getRequestBody(),
                    file.name.getRequestBody(),
                    file.getMultiPartBody("File"),
                )
            }
        }.safeApiCall(ApiEndPoints.API_CONTENT_UPLOAD).flowOn(Dispatchers.IO)
    }

    override suspend fun contentUploadText(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        uploadType: Int,
        text: String,
        duration: Int,
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return apiService.contentUploadText(
                    courseId.getRequestBody(),
                    sectionId.getRequestBody(),
                    lectureId.getRequestBody(),
                    uploadType.getRequestBody(),
                    text.getRequestBody(),
                    duration.getRequestBody()

                )
            }
        }.safeApiCall(ApiEndPoints.API_CONTENT_UPLOAD).flowOn(Dispatchers.IO)
    }
}
