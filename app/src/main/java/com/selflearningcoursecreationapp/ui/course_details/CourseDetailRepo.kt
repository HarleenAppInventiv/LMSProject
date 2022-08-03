package com.selflearningcoursecreationapp.ui.course_details

import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import kotlinx.coroutines.flow.Flow

interface CourseDetailRepo {

    suspend fun getCourseDetail(courseId: Int?): Flow<Resource>
    suspend fun getCourseSections(courseId: Int?): Flow<Resource>
    suspend fun addReviewsRequest(data: AddReviewRequestModel): Flow<Resource>
    suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun downloadCertificate(courseId: String): Flow<Resource>
    suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun contentAssessment(courseId: String): Flow<Resource>

}