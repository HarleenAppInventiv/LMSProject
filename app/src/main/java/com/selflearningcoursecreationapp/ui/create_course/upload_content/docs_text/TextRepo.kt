package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface TextRepo {
    suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getLectureDetail(lectureId: Int): Flow<Resource>
    suspend fun contentUploadText(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        uploadType: Int,
        text: String,
        duration: Int,
    ): Flow<Resource>

}