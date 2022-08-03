package com.selflearningcoursecreationapp.ui.profile.bookmark

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WishListViewModel(private val repo: WishListRepository) : BaseViewModel() {
    private val modificationEvents = MutableStateFlow<List<PagerViewEvents>>(emptyList())

    val courseLiveData = MutableLiveData<PagingData<CourseData>>()
    var purchaseCourseLiveData = MutableLiveData<OrderData>().apply {
        value = OrderData()
    }

    suspend fun getWishList(): LiveData<PagingData<CourseData>> {
        val response = repo.getWishListData().cachedIn(viewModelScope).asFlow()
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }


            }
        courseLiveData.value = response.asLiveData().value
        return response.asLiveData()
    }

    fun onViewEvent(pagerViewEvents: PagerViewEvents) {
        modificationEvents.value += pagerViewEvents
    }


    suspend fun getReviewList(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {


        val response = repo.getReviewListData(data).cachedIn(viewModelScope).asFlow()
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }


            }
        return response.asLiveData()
    }

    fun purchaseCourse(wishListItem: CourseData) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Int>()
            map["courseId"] = wishListItem.courseId ?: 0
            map["courseType"] = wishListItem.courseTypeId ?: 0
            val response = repo.purchaseCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<OrderData>).resource
                        purchaseCourseLiveData.postValue(resource)
                        onViewEvent(PagerViewEvents.Buy(resource?.courseId ?: 0))

                    }
                    updateResponseObserver(it)
                }
            }
        }


    fun buyRazorPayCourse(wishListItem: CourseData) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()


            map["courseId"] = wishListItem.courseId!!.toInt()
            map["amount"] = wishListItem.courseFee ?: ""
            map["currency"] = "INR"


            val response = repo.buyRazorPayCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

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


sealed class PagerViewEvents {
    data class Edit(val pagerViewData: CourseData) : PagerViewEvents()
    data class Remove(val pagerViewData: CourseData) : PagerViewEvents()
    data class EditLike(val pagerViewData: CourseData) : PagerViewEvents()
    data class EditDisLike(val pagerViewData: CourseData) : PagerViewEvents()
    data class EditListenData(val pagerViewData: CourseData) : PagerViewEvents()
    data class Buy(val courseId: Int) : PagerViewEvents()


}


