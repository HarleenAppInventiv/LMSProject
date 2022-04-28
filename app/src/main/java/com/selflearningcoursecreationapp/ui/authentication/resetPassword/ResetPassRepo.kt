package com.selflearningcoursecreationapp.ui.authentication.resetPassword

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ResetPassRepo {
    fun resetPass(map: HashMap<String, Any>): Flow<Resource>
}