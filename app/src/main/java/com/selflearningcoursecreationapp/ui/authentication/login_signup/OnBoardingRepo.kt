package com.selflearningcoursecreationapp.ui.authentication.login_signup

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface OnBoardingRepo {

    suspend fun allStates(): Flow<Resource>

}