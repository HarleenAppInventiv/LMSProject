package com.selflearningcoursecreationapp.ui.dashboard


import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import kotlinx.coroutines.flow.Flow

interface LearnerDashboardRepo {

    suspend fun courseStatCount(): Flow<Resource>

    suspend fun getEnrolledCourses(map: GetReviewsRequestModel): Flow<Resource>

    suspend fun getFilteredCount(map: GetReviewsRequestModel): Flow<Resource>
    suspend fun getTrackUserActivity(startDate: String, endDate: String): Flow<Resource>

}