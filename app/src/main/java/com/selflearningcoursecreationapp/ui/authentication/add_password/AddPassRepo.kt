package com.selflearningcoursecreationapp.ui.authentication.add_password

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface AddPassRepo {
    suspend fun addPass(map: HashMap<String, Any>): Flow<Resource>

}