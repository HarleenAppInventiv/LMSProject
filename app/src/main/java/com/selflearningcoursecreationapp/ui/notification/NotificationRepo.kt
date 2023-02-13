package com.selflearningcoursecreationapp.ui.notification

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {
    suspend fun getNotification(): Flow<Resource>
    suspend fun patchNotification(notificationId: Int): Flow<Resource>
    suspend fun deleteNotification(notificationId: Int): Flow<Resource>
    suspend fun readAllNotification(): Flow<Resource>
    suspend fun deleteAllNotification(): Flow<Resource>
    suspend fun myCategories(): Flow<Resource>
}