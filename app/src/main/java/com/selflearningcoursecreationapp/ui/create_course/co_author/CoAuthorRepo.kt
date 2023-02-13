package com.selflearningcoursecreationapp.ui.create_course.co_author

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface CoAuthorRepo {
    suspend fun inviteCoAuthor(map: HashMap<String, Any>): Flow<Resource>
    suspend fun existsCoAuthor(courseId: Int): Flow<Resource>
}