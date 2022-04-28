package com.selflearningcoursecreationapp.ui.authentication.login_signup

import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface OnBoardingRepo {

    suspend fun signUpApi(map: UserProfile): Flow<Resource>
    suspend fun loginInApi(map: HashMap<String, Any>): Flow<Resource>
    suspend fun professionApi(): Flow<Resource>

}