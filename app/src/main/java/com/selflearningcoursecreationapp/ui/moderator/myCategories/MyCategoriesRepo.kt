package com.selflearningcoursecreationapp.ui.moderator.myCategories

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface MyCategoriesRepo {
    suspend fun myCategories(): Flow<Resource>

}