package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.PagerViewEventsRequest
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ModHomeVM(val repo: ModHomeRepo) : BaseViewModel() {

    var selectedTabPosition = 0
    var currentPage = 1
    var isTextSearch: Boolean = false

    private var totalPage = 2
    val requestLiveData = MutableLiveData<PagingData<CourseData>>()
    var filterData = GetReviewsRequestModel()
    val refreshData = MutableLiveData<Boolean>().apply {
        value = true
    }

    val switchTab = MutableLiveData<Boolean>().apply {
        value = false
    }

    val modificationEvents = MutableStateFlow<List<PagerViewEventsRequest>>(emptyList())


    override fun onApiRetry(apiCode: String) {
    }

    fun reset() {
        currentPage = 1
        totalPage = 2
//        requestLiveData.value?.clear()
    }

    fun getNotificationCount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.getNotificationCount()
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }

    fun applyEvents(
        paging: PagingData<CourseData>,
        PagerViewEventsRequest: PagerViewEventsRequest,
    ): PagingData<CourseData> {
        return when (PagerViewEventsRequest) {
            is PagerViewEventsRequest.Remove -> {
                refreshData.postValue(true)

                paging
                    .filter { PagerViewEventsRequest.pagerViewData.courseId != it.courseId }
            }
            is PagerViewEventsRequest.Edit -> {


                paging
                    .map {
                        if (PagerViewEventsRequest.pagerViewData.courseId == it.courseId) return@map it.copy(
                            name = it.name,
                            courseRating = it.courseRating,
                            courseId = it.courseId,
                            contentDescription = it.contentDescription,
                            totalLikes = it.totalLikes,
                            totalDislikes = it.totalDislikes,
                            createdDate = it.createdDate,

                            ).also { data ->
                            data.userCourseStatus = it.userCourseStatus
                        }
                        else return@map it
                    }

            }
            is PagerViewEventsRequest.Buy -> {


                paging
                    .map {
                        if (PagerViewEventsRequest.courseId == it.courseId) return@map it.copy(
                            name = it.name,
                            courseRating = it.courseRating,
                            courseId = it.courseId,
                            contentDescription = it.contentDescription,
                            totalLikes = it.totalLikes,
                            totalDislikes = it.totalDislikes,
                            createdDate = it.createdDate,

                            ).also { data ->
                            data.userCourseStatus = 1
                        }
                        else return@map it
                    }

            }

            is PagerViewEventsRequest.EditListenData -> {


                paging
                    .map {
                        if (PagerViewEventsRequest.pagerViewData.courseId == it.courseId && PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId) return@map it.copy(
                            courseRating = it.courseRating,
                            courseId = it.courseId,
                            contentDescription = it.contentDescription,
                            totalLikes = PagerViewEventsRequest.pagerViewData.totalLikes,
                            totalDislikes = PagerViewEventsRequest.pagerViewData.totalDislikes,
                            createdDate = it.createdDate,
                            userDisLiked = PagerViewEventsRequest.pagerViewData.userDisLiked,
                            reviewId = it.reviewId

                        )
                        else return@map it
                    }

            }

            is PagerViewEventsRequest.EditLike -> {
                if (!SelfLearningApplication.token.isNullOrEmpty()) {
                    paging.map {
                        if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userLiked == 0 && PagerViewEventsRequest.pagerViewData.userDisLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.plus(1),
                                totalDislikes = it.totalDislikes?.minus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 0,
                                reviewId = it.reviewId
                            )
                        } else if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userLiked == 0) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.plus(1),
                                totalDislikes = it.totalDislikes,
                                createdDate = it.createdDate,
                                userDisLiked = 0,
//                                userLiked = 1,
                                reviewId = it.reviewId
                            )

                        } else if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userDisLiked == 1 && PagerViewEventsRequest.pagerViewData.userLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.minus(1),
                                totalDislikes = it.totalDislikes?.plus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 1,
//                                userLiked = 0,
                                reviewId = it.reviewId
                            )
                        } else if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.minus(1),
                                totalDislikes = it.totalDislikes,
                                createdDate = it.createdDate,
                                userDisLiked = it.userDisLiked,
//                                userLiked = it.userLiked?.minus(1),
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

            is PagerViewEventsRequest.EditDisLike -> {
                if (!SelfLearningApplication.token.isNullOrEmpty()) {

                    paging.map {
                        if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userLiked == 1 && PagerViewEventsRequest.pagerViewData.userDisLiked == 0) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.minus(1),
                                totalDislikes = it.totalDislikes?.plus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 1,
//                                userLiked = 0,
                                reviewId = it.reviewId
                            )
                        } else if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userDisLiked == 0) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes,
                                totalDislikes = it.totalDislikes?.plus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 1,
//                                userLiked = 0,
                                reviewId = it.reviewId
                            )

                        } else if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userDisLiked == 1 && PagerViewEventsRequest.pagerViewData.userLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes?.plus(1),
                                totalDislikes = it.totalDislikes?.minus(1),
                                createdDate = it.createdDate,
                                userDisLiked = 0,
//                                userLiked = 1,
                                reviewId = it.reviewId
                            )
                        } else if (PagerViewEventsRequest.pagerViewData.reviewId == it.reviewId && PagerViewEventsRequest.pagerViewData.userDisLiked == 1) {

                            return@map it.copy(

                                name = it.name,
                                courseRating = it.courseRating,
                                courseId = it.courseId,
                                contentDescription = it.contentDescription,
                                totalLikes = it.totalLikes,
                                totalDislikes = it.totalDislikes?.minus(1),
                                createdDate = it.createdDate,
                                userDisLiked = it.userDisLiked?.minus(1),
//                                userLiked = it.userLiked,
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

    fun onViewEvent(pagerViewEvents: PagerViewEventsRequest) {
        modificationEvents.value += pagerViewEvents
    }

}