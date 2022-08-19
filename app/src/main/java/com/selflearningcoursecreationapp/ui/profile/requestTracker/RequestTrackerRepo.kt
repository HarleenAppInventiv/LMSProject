package com.selflearningcoursecreationapp.ui.profile.requestTracker

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import kotlinx.coroutines.flow.Flow

interface RequestTrackerRepo {
    suspend fun modStatus(): Flow<Resource>
    suspend fun requestCount(): Flow<Resource>
    suspend fun getRequestList(hashmap: HashMap<String, Int>): LiveData<PagingData<CourseData>>
    suspend fun manageInvitation(map: HashMap<String, Any>): Flow<Resource>


}