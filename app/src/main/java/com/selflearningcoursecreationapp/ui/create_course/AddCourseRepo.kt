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

}