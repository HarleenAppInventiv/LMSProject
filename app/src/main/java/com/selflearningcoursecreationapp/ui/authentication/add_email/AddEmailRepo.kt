package com.selflearningcoursecreationapp.ui.authentication.add_email

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface AddEmailRepo {
    suspend fun addEmail(map: HashMap<String, Any>): Flow<Resource>

}