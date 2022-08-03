package com.selflearningcoursecreationapp.ui.course_details

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.ContentAssessmentData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class CourseDetailRepoImpl(private var apiService: ApiService) : CourseDetailRepo {
    override suspend fun getCourseDetail(courseId: Int?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CourseData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CourseData>> {
                return apiService.getCourseDetails(courseId)
            }

        }.safeApiCall(ApiEndPoints.API_COURSE_DETAILS).flowOn(Dispatchers.IO)

    }

    override suspend fun getCourseSections(courseId: Int?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<SectionModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<SectionModel>>> {
                return apiService.getViewCourseSections(courseId)
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

    override suspend fun contentAssessment(courseId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ContentAssessmentData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ContentAssessmentData>> {
                return apiService.assessmentdetail(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_DETAIL).flowOn(Dispatchers.IO)
    }
}