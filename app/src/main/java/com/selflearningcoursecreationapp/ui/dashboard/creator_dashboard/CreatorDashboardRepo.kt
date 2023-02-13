package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard


import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import kotlinx.coroutines.flow.Flow

interface CreatorDashboardRepo {

    suspend fun audienceStatCount(): Flow<Resource>

    suspend fun creatorDashTotalEarning(map: GetReviewsRequestModel): Flow<Resource>

    suspend fun filteredCourseUserCount(map: GetReviewsRequestModel): Flow<Resource>

    suspend fun courseUserCount(map: GetReviewsRequestModel): Flow<Resource>

    suspend fun getMasterData(): Flow<Resource>
}