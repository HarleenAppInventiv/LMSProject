package com.selflearningcoursecreationapp.ui.create_course

import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.SingleClickResponse
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.ExistsCoAuthorResponse
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
                return apiService.addCourseStep1(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_1).flowOn(Dispatchers.IO)
    }

    override suspend fun step1UpdateCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.updateCourseStep1(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_1).flowOn(Dispatchers.IO)
    }

    override suspend fun step2AddCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.addCourseStep2(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_2,
                    map
                )
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

    override suspend fun getCourseDetail(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.getCourseDetail(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
                    courseId
                )
            }
        }.safeApiCall(ApiEndPoints.API_CRE_STEP_1 + "/detail").flowOn(Dispatchers.IO)

    }

    override suspend fun getCourseSections(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.getCourseSections(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_GET_SECTIONS,
                    courseId
                )
            }
        }.safeApiCall(ApiEndPoints.API_GET_SECTIONS).flowOn(Dispatchers.IO)

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
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_UPLOAD_BANNER,
                    filePart,
                    body,
                    courseId
                ) else apiService.uploadCourseLogo(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_UPLOAD_LOGO,
                    filePart,
                    body,
                    courseId
                )
            }
        }.safeApiCall(ApiEndPoints.API_UPLOAD_IMAGE).flowOn(Dispatchers.IO)
    }

    override suspend fun addSection(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<SectionModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<SectionModel>> {
                return apiService.addSection(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_SECTION,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_SECTION).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteSection(courseId: String, sectionId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<SectionModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<SectionModel>> {
                return apiService.deleteSection(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_SECTION,
                    courseId,
                    sectionId
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_SECTION + "/delete").flowOn(Dispatchers.IO)
    }

    override suspend fun dragAndDropSection(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.dragAndDropSection(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_SECTION_DRAG_DROP,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_SECTION_DRAG_DROP).flowOn(Dispatchers.IO)
    }

    override suspend fun addLecture(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.addLecture(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_LECTURE_POST,
                    map
                )
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
                return apiService.deleteLecture(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_LECTURE_DELETE,
                    courseId,
                    sectionId,
                    lectureId
                )
            }
        }.safeApiCall(ApiEndPoints.API_LECTURE_DELETE + "/delete").flowOn(Dispatchers.IO)
    }


    override suspend fun dragAndDropLecture(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.dragAndDropLecture(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_LECTURE_DRAG_DROP,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_LECTURE_DRAG_DROP).flowOn(Dispatchers.IO)
    }

    override suspend fun getAssessmentQues(assessmentId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getAssessmentQues(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT,
                    assessmentId
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT).flowOn(Dispatchers.IO)

    }

    override suspend fun deleteAssessment(
        assessmentId: Int,
        courseID: Int,
        show: Boolean
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.deleteAssessment(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT,
                    assessmentId,
                    courseID
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT + "/delete/$show").flowOn(Dispatchers.IO)

    }

    override suspend fun publishCourse(data: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.publishCourse(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_PUBLISH_COURSE,
                    data
                )
            }
        }.safeApiCall(ApiEndPoints.API_PUBLISH_COURSE).flowOn(Dispatchers.IO)

    }

    override suspend fun getKeywords(data: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<SingleClickResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<SingleClickResponse>> {
                return apiService.getKeywords(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_GET_KEYWORDS,
                    data
                )
            }
        }.safeApiCall(ApiEndPoints.API_GET_KEYWORDS).flowOn(Dispatchers.IO)

    }

    override suspend fun addPatchSection(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<SectionModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<SectionModel>> {
                return apiService.addPatchSection(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_SECTION,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_SECTION + "/patch").flowOn(Dispatchers.IO)
    }


    override suspend fun updateCoAuthorStatus(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.manageCoAuthorInvitation(map)
            }

        }.safeApiCall(ApiEndPoints.API_COAUTHOR_INVITATION).flowOn(Dispatchers.IO)

    }

    override suspend fun getCompleteCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.getCompleteStatus(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_COMPLETE_STATUS,
                    map
                )
            }
        }.safeApiCall(ApiEndPoints.API_COMPLETE_STATUS).flowOn(Dispatchers.IO)

    }


    override suspend fun existsCoAuthor(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ExistsCoAuthorResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ExistsCoAuthorResponse>> {
                return apiService.existsCoAuthorDetails(courseId)
            }

        }.safeApiCall(ApiEndPoints.API_EXISTS_COAUTHOR).flowOn(Dispatchers.IO)
    }

}