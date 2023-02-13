package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ModCourseDetailRepo {
    suspend fun modCourseDetail(map: HashMap<String, Any>, status: Int): Flow<Resource>
    suspend fun getAssessment(assessmentId: Int): Flow<Resource>
    suspend fun addComment(map: HashMap<String, Any>, type: Int): Flow<Resource>
    suspend fun addCommentLec(map: HashMap<String, Any>, type: Int): Flow<Resource>
    suspend fun addCommentMisc(map: HashMap<String, Any>, type: Int): Flow<Resource>
    suspend fun updateCourseRequest(map: Map<String, Any>): Flow<Resource>
    suspend fun updateCourseStatus(map: Map<String, Any>): Flow<Resource>
    suspend fun getCourseSections(courseId: Int, status: Int): Flow<Resource>

//    suspend fun deleteComment(map: HashMap<String, Any>): Flow<Resource>

}