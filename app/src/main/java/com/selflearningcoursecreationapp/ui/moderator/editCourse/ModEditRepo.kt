package com.selflearningcoursecreationapp.ui.moderator.editCourse

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ModEditRepo {
    suspend fun editCourse(map: HashMap<String, Any>): Flow<Resource>

}