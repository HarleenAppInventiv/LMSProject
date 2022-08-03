package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface HomeFilterRepo {

    suspend fun getFilters(): Flow<Resource>
    suspend fun getRatingFilters(courseId: Int): Flow<Resource>
}