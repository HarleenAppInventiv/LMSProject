package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseComments
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ModCourseDetailRepoImp(private val apiService: ApiService) : ModCourseDetailRepo {
    override suspend fun modCourseDetail(map: HashMap<String, Any>, status: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.getModCourse(map)
//                if (status == CreatedCourseStatus.PUBLISHED) apiService.getCourseDetails(
//                    map
//                ) else apiService.getDetailCreator(map)
            }

        }.safeApiCall(ApiEndPoints.API_MOD_COURSE_DETAIL).flowOn(Dispatchers.IO)

    }

    override suspend fun getAssessment(assessmentId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getAssessmentQues(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT,
                    assessmentId
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT).flowOn(Dispatchers.IO)
    }

    override suspend fun addComment(map: HashMap<String, Any>, type: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<SectionModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<SectionModel>> {
                return when (type) {
                    Constant.CLICK_ADD_COMMENT -> {
                        apiService.addCommentSection(map)
                    }
                    Constant.CLICK_EDIT_COMMENT -> {
                        apiService.editCommentSection(map)
                    }
                    Constant.CLICK_DELETE_COMMENT -> {
                        apiService.deleteCommentSection(map)
                    }
                    else -> {
                        apiService.addCommentSection(map)
                    }
                }
            }
        }.safeApiCall(ApiEndPoints.API_COMMENT_SECTION).flowOn(Dispatchers.IO)
    }

    override suspend fun addCommentMisc(map: HashMap<String, Any>, type: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseComments>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseComments>> {
                return when (type) {
                    Constant.CLICK_ADD_COMMENT -> {
                        apiService.addCommentMisc(map)
                    }
                    Constant.CLICK_EDIT_COMMENT -> {
                        apiService.editCommentMisc(map)
                    }
                    Constant.CLICK_DELETE_COMMENT -> {
                        apiService.deleteCommentMisc(map)
                    }
                    else -> {
                        apiService.addCommentMisc(map)
                    }
                }
            }
        }.safeApiCall(ApiEndPoints.API_COMMENT_MISC).flowOn(Dispatchers.IO)
    }

    override suspend fun addCommentLec(map: HashMap<String, Any>, type: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return when (type) {
                    Constant.CLICK_ADD_COMMENT_LEC -> {
                        apiService.addCommentLecture(map)
                    }
                    Constant.CLICK_EDIT_COMMENT_LEC -> {
                        apiService.editCommentLecture(map)
                    }
                    Constant.CLICK_DELETE_COMMENT_LEC -> {
                        apiService.deleteCommentLecture(map)
                    }
                    else -> {
                        apiService.addCommentLecture(map)
                    }
                }
            }
        }.safeApiCall(ApiEndPoints.API_COMMENT_LECTURE).flowOn(Dispatchers.IO)
    }

    override suspend fun updateCourseRequest(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.updateCourseRequest(map)
            }
        }.safeApiCall(ApiEndPoints.API_UPDATE_MOD_REQUEST).flowOn(Dispatchers.IO)
    }

    override suspend fun updateCourseStatus(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.updateCourseStatus(map)
            }
        }.safeApiCall(ApiEndPoints.API_UPDATE_MOD_COURSE_STATUS).flowOn(Dispatchers.IO)
    }

    override suspend fun getCourseSections(courseId: Int, status: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<SectionModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<SectionModel>>> {
                return /*when (status) {
                    CreatedCourseStatus.PUBLISHED -> apiService.getViewCourseSections(courseId)
                    else ->*/ apiService.getSectionLecture(courseId)
                /*}*/


            }

        }.safeApiCall(ApiEndPoints.API_COURSE_DETAILS).flowOn(Dispatchers.IO)

    }
}

