package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AudioLessonRepo {
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

}