package com.selflearningcoursecreationapp.ui.profile.reward.viewModel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.profile.bookmark.PagerViewEvents
import com.selflearningcoursecreationapp.ui.profile.reward.RewardsRepository
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class RewardViewModel(private val repo: RewardsRepository) : BaseViewModel() {
    private val modificationEvents = MutableStateFlow<List<PagerViewEvents>>(emptyList())
    var rewardPoints = MutableLiveData<Long>()


    suspend fun getRewardList(model: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        val response = repo.getRewardPoints(model).cachedIn(viewModelScope).asFlow()
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }


            }
        return response.asLiveData()
    }

    fun onViewEvent(pagerViewEvents: PagerViewEvents) {
        modificationEvents.value += pagerViewEvents
    }

    override fun onApiRetry(apiCode: String) {


    }

    fun applyEvents(
        paging: PagingData<CourseData>,
        pagerViewEvents: PagerViewEvents
    ): PagingData<CourseData> {
        return when (pagerViewEvents) {
            is PagerViewEvents.Remove -> {
                paging
                    .filter { pagerViewEvents.pagerViewData.courseId != it.courseId }

            }
            is PagerViewEvents.Edit -> {


                paging
                    .map {
                        if (pagerViewEvents.pagerViewData.courseId == it.courseId) return@map it.copy(
                            name = it.name,
                            courseRating = it.courseRating,
                            courseId = it.courseId,
                            contentDescription = it.contentDescription,
                            totalLikes = it.totalLikes,
                            totalDislikes = it.totalDislikes,
                            createdDate = it.createdDate,

                            )
                        else return@map it
                    }

            }
            is PagerViewEvents.Buy -> {


                paging
                    .map {
                        if (pagerViewEvents.courseId == it.courseId) return@map it.copy(
                            name = it.name,
                            courseRating = it.courseRating,
                            courseId = it.courseId,
                            contentDescription = it.contentDescription,
                            totalLikes = it.totalLikes,
                            totalDislikes = it.totalDislikes,
                            createdDate = it.createdDate,
                            userCourseStatus = 1
                        )
                        else return@map it
                    }

            }

            is PagerViewEvents.EditListenData -> {


                paging
                    .map {
                        if (pagerViewEvents.pagerViewData.courseId == it.courseId && pagerViewEvents.pagerViewData.reviewId == it.reviewId) return@map it.copy(
                            courseRating = it.courseRating,
                            courseId = it.courseId,
                            contentDescription = it.contentDescription,
                            totalLikes = pagerViewEvents.pagerViewData.totalLikes,
                            totalDislikes = pagerViewEvents.pagerViewData.totalDislikes,
                            createdDate = it.createdDate,
                            userDisLiked = pagerViewEvents.pagerViewData.userDisLiked,
                            userLiked = pagerViewEvents.pagerViewData.userLiked,
                            reviewId = it.reviewId

                        )
                        else return@map it
                    }

            }

            is PagerViewEvents.EditLike -> {
                if (!SelfLearningApplication.token.isNullOrEmpty()) {
                    paging.map {
                        if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userLiked == 0 && pagerViewEvents.pagerViewData.userDisLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.plus(1),
                                totalDislikes = it.totalDislikes?.minus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 0,
                                userLiked = 1,
                                reviewId = it.reviewId
                            )
                        } else if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userLiked == 0) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.plus(1),
                                totalDislikes = it.totalDislikes,
                                createdDate = it.createdDate,
                                userDisLiked = 0,
                                userLiked = 1,
                                reviewId = it.reviewId
                            )

                        } else if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userDisLiked == 1 && pagerViewEvents.pagerViewData.userLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.minus(1),
                                totalDislikes = it.totalDislikes?.plus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 1,
                                userLiked = 0,
                                reviewId = it.reviewId
                            )
                        } else if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.minus(1),
                                totalDislikes = it.totalDislikes,
                                createdDate = it.createdDate,
                                userDisLiked = it.userDisLiked,
                                userLiked = it.userLiked?.minus(1),
                                reviewId = it.reviewId
                            )
                        } else {
                            return@map it
                        }
                    }
                } else {
                    updateResponseObserver(Resource.Success(true, ApiEndPoints.GUEST_LOGIN))
                    return paging
                }
            }

            is PagerViewEvents.EditDisLike -> {
                if (!SelfLearningApplication.token.isNullOrEmpty()) {

                    paging.map {
                        if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userLiked == 1 && pagerViewEvents.pagerViewData.userDisLiked == 0) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.minus(1),
                                totalDislikes = it.totalDislikes?.plus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 1,
                                userLiked = 0,
                                reviewId = it.reviewId
                            )
                        } else if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userDisLiked == 0) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes,
                                totalDislikes = it.totalDislikes?.plus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 1,
                                userLiked = 0,
                                reviewId = it.reviewId
                            )

                        } else if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userDisLiked == 1 && pagerViewEvents.pagerViewData.userLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.plus(1),
                                totalDislikes = it.totalDislikes?.minus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 0,
                                userLiked = 1,
                                reviewId = it.reviewId
                            )
                        } else if (pagerViewEvents.pagerViewData.reviewId == it.reviewId && pagerViewEvents.pagerViewData.userDisLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes,
                                totalDislikes = it.totalDislikes?.minus(1),
                                createdDate = it.createdDate,
                                userDisLiked = it.userDisLiked?.minus(1),
                                userLiked = it.userLiked,
                                reviewId = it.reviewId
                            )

                        } else {
                            return@map it
                        }
                    }
                } else {
                    updateResponseObserver(Resource.Success(true, ApiEndPoints.GUEST_LOGIN))
                    return paging
                }
            }
        }

    }

}




