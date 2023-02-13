package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import kotlinx.coroutines.flow.Flow

interface ModHomeRepo {
    suspend fun courseRequest(
        data: GetReviewsRequestModel
    ): LiveData<PagingData<CourseData>>

    suspend fun updateCourseRequest(map: Map<String, Any>): Flow<Resource>
    suspend fun updateCourseStatus(map: Map<String, Any>): Flow<Resource>
    suspend fun getNotificationCount(): Flow<Resource>

    suspend fun modCourse(
        data: GetReviewsRequestModel
    ): LiveData<PagingData<CourseData>>
}