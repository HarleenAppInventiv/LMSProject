package com.selflearningcoursecreationapp.ui.profile.profileDetails

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileDetailRepo {
    suspend fun viewProfile(): Flow<Resource>
    suspend fun uploadImage(body: MultipartBody.Part, fullName: RequestBody): Flow<Resource>

}