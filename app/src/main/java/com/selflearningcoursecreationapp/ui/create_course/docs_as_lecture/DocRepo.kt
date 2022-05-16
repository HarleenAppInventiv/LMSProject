package com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface DocRepo {
    suspend fun addPatchLecture(map: HashMap<String, Any>): Flow<Resource>

}