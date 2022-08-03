package com.selflearningcoursecreationapp.ui.profile.edit_profile

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow


interface EditProfileRepo {
    suspend fun updateProfile(map: HashMap<String, Any>): Flow<Resource>
    suspend fun stateList(): Flow<Resource>
    suspend fun cityList(stateId: Int): Flow<Resource>
}