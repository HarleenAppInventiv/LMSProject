package com.selflearningcoursecreationapp.ui.profile.profileDetails

import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.moderator.model.AddModeratorResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileDetailRepo {
    suspend fun viewProfile(): Flow<Resource>
    suspend fun uploadImage(body: MultipartBody.Part, fullName: RequestBody): Flow<Resource>
    suspend fun uploadModeDoc(body: MultipartBody.Part): Flow<Resource>
    suspend fun addModerator(body: AddModeratorResponse): Flow<Resource>

}