package com.selflearningcoursecreationapp.ui.bottom_home.categories

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface HomeCategoryRepo {
    suspend fun getCategories(): Flow<Resource>
}