package com.selflearningcoursecreationapp.ui.home

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface HomeActivityRepo {
    suspend fun manageCouthorInvitation(map: HashMap<String, Any>): Flow<Resource>
    suspend fun purchaseCourse(map: HashMap<String, Int>): Flow<Resource>
    suspend fun switchMod(userMode: Int): Flow<Resource>
    suspend fun patchNotification(notificationId: Int): Flow<Resource>
    suspend fun profileApi(): Flow<Resource>
    suspend fun courseStatusPolicy(map: HashMap<String, Any>): Flow<Resource>
    suspend fun myCategories(): Flow<Resource>

}