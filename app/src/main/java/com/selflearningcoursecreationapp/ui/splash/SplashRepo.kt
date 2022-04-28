package com.selflearningcoursecreationapp.ui.splash

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface SplashRepo {
    suspend fun profileApi(): Flow<Resource>
}