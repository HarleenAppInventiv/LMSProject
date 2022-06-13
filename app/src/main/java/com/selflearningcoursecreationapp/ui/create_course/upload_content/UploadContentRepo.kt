package com.selflearningcoursecreationapp.ui.create_course.upload_content

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UploadContentRepo {
    suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getLectureDetail(lectureId: Int): Flow<Resource>
    suspend fun contentUpload(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        file: File,
        uploadType: Int,
        text: String,
        duration: Int,
    ): Flow<Resource>

    suspend fun thumbnailUpload(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        file: File,
    ): Flow<Resource>

    suspend fun contentUploadText(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        uploadType: Int,
        text: String,
        duration: Int,
    ): Flow<Resource>
}