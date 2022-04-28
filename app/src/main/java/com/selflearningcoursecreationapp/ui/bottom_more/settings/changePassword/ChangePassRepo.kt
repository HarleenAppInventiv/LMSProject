package com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ChangePassRepo {
    suspend fun changePass(map: HashMap<String, Any>): Flow<Resource>
}