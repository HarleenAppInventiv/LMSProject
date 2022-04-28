package com.selflearningcoursecreationapp.ui.profile.profileDetails

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileDetailRepo {
    suspend fun viewProfile(): Flow<Resource>

}