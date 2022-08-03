package com.selflearningcoursecreationapp.ui.dialog.unlockCourse

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface UnlockRepo {
    suspend fun unlockCourse(map: HashMap<String, Any>): Flow<Resource>


}