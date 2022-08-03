package com.selflearningcoursecreationapp.ui.home

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface HomeActivityRepo {
    suspend fun manageCouthorInvitation(map: HashMap<String, Any>): Flow<Resource>
    suspend fun purchaseCourse(map: HashMap<String, Int>): Flow<Resource>

}