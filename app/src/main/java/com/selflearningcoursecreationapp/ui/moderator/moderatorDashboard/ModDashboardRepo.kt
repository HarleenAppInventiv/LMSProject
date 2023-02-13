package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard


import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import kotlinx.coroutines.flow.Flow

interface ModDashboardRepo {

    suspend fun courseStatCount(): Flow<Resource>

    suspend fun acceptedCourses(map: GetReviewsRequestModel): Flow<Resource>

    suspend fun getFilteredCount(map: GetReviewsRequestModel): Flow<Resource>


}