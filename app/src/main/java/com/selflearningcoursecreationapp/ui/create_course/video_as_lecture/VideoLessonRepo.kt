package com.selflearningcoursecreationapp.ui.create_course.video_as_lecture

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface VideoLessonRepo {
    suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getLectureDetail(lectureId: Int): Flow<Resource>
    suspend fun contentUpload(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        fileName: String,
        file: File,
        uploadType: Int,
        text: String,
        duration: Int,
    ): Flow<Resource>

    suspend fun thumbnailUpload(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        fileName: String,
        file: File,
    ): Flow<Resource>
}