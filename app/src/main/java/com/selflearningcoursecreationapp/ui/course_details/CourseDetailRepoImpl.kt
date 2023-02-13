package com.selflearningcoursecreationapp.ui.course_details

import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.ContentAssessmentData
import com.selflearningcoursecreationapp.models.StateModel
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.ui.course_details.model.ShareLinkUrl
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class CourseDetailRepoImpl(private var apiService: ApiService) : CourseDetailRepo {
    override suspend fun getCourseDetail(
        courseId: Int,
        type: String,
        moderatorRequestId: String
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                val hashMap = HashMap<String, Any>()
                hashMap.put("CourseId", courseId)
                return when (type) {
                    "moderatorComments" -> {
                        hashMap.put("ModeratorRequestId", moderatorRequestId)
                        apiService.getDetailRequestTracker(hashMap)
                    }
                    "creator" -> apiService.getDetailCreator(hashMap)
                    else -> apiService.getCourseDetails(hashMap)
                }
            }

        }.safeApiCall(ApiEndPoints.API_COURSE_DETAILS).flowOn(Dispatchers.IO)

    }

    override suspend fun getCourseSections(
        courseId: Int,
        type: String,
        moderatorRequestId: String
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<SectionModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<SectionModel>>> {
                val hashMap = HashMap<String, Any>()
                hashMap.put("CourseId", courseId)
                return when (type) {
                    "moderatorComments" -> {
                        hashMap.put("ModeratorRequestId", moderatorRequestId)
                        apiService.getSectionLectureForRequestTracker(hashMap)
                    }
                    "creator" -> apiService.getSectionLecture(courseId)
                    else -> apiService.getViewCourseSections(courseId)
                }


            }

        }.safeApiCall(ApiEndPoints.API_COURSE_DETAILS).flowOn(Dispatchers.IO)

    }

    override suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.purchaseCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_PURCHASE_COURSE).flowOn(Dispatchers.IO)
    }

    override suspend fun downloadCertificate(courseId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.getCertificate(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_CERTIFICATE).flowOn(Dispatchers.IO)
    }

    override suspend fun downloadAppreciationCertificate(courseId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.getAppCertificate(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_APP_CERTIFICATE).flowOn(Dispatchers.IO)
    }

    override suspend fun addReviewsRequest(data: AddReviewRequestModel): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.getAddReview(data)
            }

        }.safeApiCall(ApiEndPoints.API_ADD_REVIEW).flowOn(Dispatchers.IO)
    }

    override suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.buyRazorPayCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_RAZORPAY_COURSE).flowOn(Dispatchers.IO)
    }

    override suspend fun getShareLink(courseId: Int?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ShareLinkUrl>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ShareLinkUrl>> {
                return apiService.getShareUrl(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_COURSE_SHARE_URL).flowOn(Dispatchers.IO)
    }

    override suspend fun contentAssessment(courseId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ContentAssessmentData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ContentAssessmentData>> {
                return apiService.assessmentdetail(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_DETAIL).flowOn(Dispatchers.IO)
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


    override suspend fun getSectionItemDetails(courseId: HashMap<String, Int>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.getContentDetails(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_GET_DETAIL_OF_CONTENT).flowOn(Dispatchers.IO)
    }

    override suspend fun getSectionItemDetailsMode(courseId: HashMap<String, Int>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.getContentDetailsCreator(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_GET_DETAIL_OF_CONTENT_MODE_CREATOR).flowOn(Dispatchers.IO)
    }

    override suspend fun getQuizQues(quizId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getQuizQues(
                    BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ,
                    quizId
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ + "/get").flowOn(Dispatchers.IO)
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


    override suspend fun stateList(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<StateModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<StateModel>>> {
                return apiService.stateList()
            }
        }.safeApiCall(ApiEndPoints.API_GET_ALL_STATES).flowOn(Dispatchers.IO)
    }

    override suspend fun getAuthorDetails(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AuthorDetailsData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AuthorDetailsData>> {
                return apiService.getAuthorDEtails(map)
            }
        }.safeApiCall(ApiEndPoints.API_COURSE_AUTHOR_DETAIL).flowOn(Dispatchers.IO)
    }

    override suspend fun sendAudioVideoTime(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.sendAudioVideoFinalTime(map)
            }
        }.safeApiCall(ApiEndPoints.API_USER_COURSE_CONTENT_HISTORY).flowOn(Dispatchers.IO)
    }


}
