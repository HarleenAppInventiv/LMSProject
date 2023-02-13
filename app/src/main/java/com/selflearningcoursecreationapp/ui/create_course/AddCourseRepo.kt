package com.selflearningcoursecreationapp.ui.create_course

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AddCourseRepo {
    suspend fun step1AddCourse(map: HashMap<String, Any>): Flow<Resource>
    suspend fun step1UpdateCourse(map: HashMap<String, Any>): Flow<Resource>
    suspend fun step2AddCourse(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getMasterData(): Flow<Resource>
    suspend fun getCourseDetail(courseId: Int): Flow<Resource>
    suspend fun getCourseSections(courseId: Int): Flow<Resource>
    suspend fun uploadCourseImage(
        filePart: MultipartBody.Part,
        body: RequestBody,
        courseId: RequestBody?,
        banner: Boolean,
    ): Flow<Resource>

    suspend fun addSection(map: HashMap<String, Any>): Flow<Resource>
    suspend fun addPatchSection(map: HashMap<String, Any>): Flow<Resource>
    suspend fun deleteSection(courseID: String, sectionID: String): Flow<Resource>
    suspend fun dragAndDropSection(map: HashMap<String, Any>): Flow<Resource>
    suspend fun addLecture(map: HashMap<String, Any>): Flow<Resource>
    suspend fun deleteLecture(
        courseID: String,
        sectionID: String,
        lectureId: String,
    ): Flow<Resource>

    suspend fun dragAndDropLecture(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getAssessmentQues(assessmentId: Int): Flow<Resource>
    suspend fun deleteAssessment(assessmentId: Int, courseID: Int, show: Boolean): Flow<Resource>
    suspend fun publishCourse(data: HashMap<String, Any>): Flow<Resource>
    suspend fun getKeywords(data: HashMap<String, Any>): Flow<Resource>
    suspend fun updateCoAuthorStatus(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getCompleteCourse(map: HashMap<String, Any>): Flow<Resource>
    suspend fun existsCoAuthor(courseId: Int): Flow<Resource>
}