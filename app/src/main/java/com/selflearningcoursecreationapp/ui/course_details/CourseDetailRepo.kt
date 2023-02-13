package com.selflearningcoursecreationapp.ui.course_details

import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import kotlinx.coroutines.flow.Flow

interface CourseDetailRepo {
    suspend fun getQuizQues(quizId: Int): Flow<Resource>
    suspend fun getCourseDetail(
        courseId: Int,
        type: String,
        moderatorRequestId: String
    ): Flow<Resource>

    suspend fun getCourseSections(
        courseId: Int,
        type: String,
        moderatorRequestId: String
    ): Flow<Resource>

    suspend fun addReviewsRequest(data: AddReviewRequestModel): Flow<Resource>
    suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun downloadCertificate(courseId: String): Flow<Resource>
    suspend fun downloadAppreciationCertificate(courseId: String): Flow<Resource>
    suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun getShareLink(courseId: Int?): Flow<Resource>
    suspend fun contentAssessment(courseId: String): Flow<Resource>
    suspend fun editCourseToDraft(courseId: Int): Flow<Resource>
    suspend fun getSectionItemDetails(courseId: HashMap<String, Int>): Flow<Resource>
    suspend fun getSectionItemDetailsMode(courseId: HashMap<String, Int>): Flow<Resource>
    suspend fun getAssessment(assessmentId: Int): Flow<Resource>
    suspend fun stateList(): Flow<Resource>
    suspend fun getAuthorDetails(map: HashMap<String, Any>): Flow<Resource>
    suspend fun sendAudioVideoTime(map: HashMap<String, Any>): Flow<Resource>

//    //creator
//    suspend fun getCreatorCourseDetail(courseId: Int?): Flow<Resource>
//    suspend fun getCreatorCourseSections(courseId: Int?): Flow<Resource>

}