package com.selflearningcoursecreationapp.ui.profile.profileThumb

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileThumbRepo {
    suspend fun logout(): Flow<Resource>
    suspend fun deleteAccount(deleteUsers: Boolean, deleteWallet: Boolean): Flow<Resource>
    suspend fun switchMod(userMode: Int): Flow<Resource>
    suspend fun changeViMode(changeViMode: ChangeViModeModel): Flow<Resource>
    suspend fun profileApi(): Flow<Resource>
}