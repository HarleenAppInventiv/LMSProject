package com.selflearningcoursecreationapp.ui.bottom_more

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface MoreFragmentRepo {
    suspend fun switchMod(userMode: Int): Flow<Resource>
    suspend fun support(map: HashMap<String, Any>): Flow<Resource>
//    suspend fun staticPages(type:Int): Flow<Resource>
//    suspend fun staticPagesFaq(): Flow<Resource>

}