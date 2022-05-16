package com.selflearningcoursecreationapp.ui.create_course.docs_text

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface TextRepo {
    suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource>

}