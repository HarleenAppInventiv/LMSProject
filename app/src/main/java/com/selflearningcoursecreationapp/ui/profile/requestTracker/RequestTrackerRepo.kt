package com.selflearningcoursecreationapp.ui.profile.requestTracker

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import kotlinx.coroutines.flow.Flow

interface RequestTrackerRepo {
    suspend fun modStatus(): Flow<Resource>
    suspend fun requestCount(): Flow<Resource>
    suspend fun cancelReq(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getRequestList(hashmap: GetReviewsRequestModel): LiveData<PagingData<CourseData>>
    suspend fun manageInvitation(map: HashMap<String, Any>): Flow<Resource>
    suspend fun paymentWithdrawList(map: HashMap<String, Any>): Flow<Resource>
    suspend fun existsCoAuthor(courseId: Int): Flow<Resource>

}