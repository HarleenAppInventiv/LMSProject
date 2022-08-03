package com.selflearningcoursecreationapp.ui.bottom_course

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface MyCoursesRepo {
    suspend fun onGoing(map: Map<String, Any>): Flow<Resource>

}