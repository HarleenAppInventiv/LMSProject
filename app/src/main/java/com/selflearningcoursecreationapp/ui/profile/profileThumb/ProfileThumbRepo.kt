package com.selflearningcoursecreationapp.ui.profile.profileThumb

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileThumbRepo {
    suspend fun logout(): Flow<Resource>
    suspend fun deleteAccount(): Flow<Resource>
    suspend fun switchMod(userMode: Int): Flow<Resource>
}