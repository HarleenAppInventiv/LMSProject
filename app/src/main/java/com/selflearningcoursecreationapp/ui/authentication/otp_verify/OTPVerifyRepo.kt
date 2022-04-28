package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface OTPVerifyRepo {
    suspend fun reqOtp(map: HashMap<String, Any>): Flow<Resource>
    suspend fun reqEmailOtp(map: HashMap<String, Any>): Flow<Resource>
    suspend fun verOtp(map: HashMap<String, Any>): Flow<Resource>
    suspend fun verEmailOtp(map: HashMap<String, Any>): Flow<Resource>
}