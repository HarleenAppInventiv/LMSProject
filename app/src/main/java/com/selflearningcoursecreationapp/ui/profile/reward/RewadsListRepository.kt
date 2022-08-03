package com.selflearningcoursecreationapp.ui.profile.reward

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel

interface RewardsRepository {
    suspend fun getRewardPoints(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>>
}

class RewardsRepositoryImpl(private val service: ApiService) : RewardsRepository {


    override suspend fun getRewardPoints(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RewardPagingDataSource(service, data)
            }
        ).liveData
    }


}