package com.selflearningcoursecreationapp.ui.profile.requestTracker

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
import com.selflearningcoursecreationapp.models.user.ModeratorStatusResponses
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.moderator.model.RequestCountModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls.PaymentWithdrawData
import com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls.PaymentWithdrawList
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MODSTATUS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RequestrackerVM(private val repo: RequestTrackerRepo) : BaseViewModel() {
    var isRejected: Boolean = false
    var modComment: Boolean = false
    var coAuthorRequestId: Int = 0
    var sentRequestId: Int = 0
    var courseId: Int = 0
    var status: Int = 0
    var position: Int = 0

    private val modificationEvents = MutableStateFlow<List<PagerViewEventsRequest>>(emptyList())


    val courseLiveData = MutableLiveData<PagingData<CourseData>>()
    var purchaseCourseLiveData = MutableLiveData<OrderData>().apply {
        value = OrderData()
    }

    var modStatusLiveData = MutableLiveData<Int>().apply {
        value = -1
    }


    var requestCountLiveData = MutableLiveData<RequestCountModel>().apply {
        value = RequestCountModel()
    }

    var currentPage = 1
    var isLastPage = false


    var paymentWithdrawLiveDataList = MutableLiveData<ArrayList<PaymentWithdrawList>>().apply {
        value = ArrayList()
    }

    fun existsCoAuthorDetails() {
        viewModelScope.launch(coroutineExceptionHandle) {

            val response = repo.existsCoAuthor(courseId ?: 0)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }

    fun getPaymentWithdrawlList() = viewModelScope.launch(coroutineExceptionHandle)
    {

        if (!isLastPage) {

            var map = HashMap<String, Any>()
            map["pageNumber"] = currentPage
            map["pageSize"] = 10
            val response = repo.paymentWithdrawList(map)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<PaymentWithdrawData>).resource?.let { resource ->

                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                paymentWithdrawLiveDataList.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = paymentWithdrawLiveDataList.value
                            list?.addAll(resource.list ?: ArrayList())
//                            if(resource.list.isNullOrEmpty())
                            isLastPage = resource.list.isNullOrEmpty()
                            paymentWithdrawLiveDataList.postValue(list)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun modStatus() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.modStatus()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<ModeratorStatusResponses>

                    var respArray = data.resource?.list ?: ArrayList()


                    if (respArray.size > 0) {
                        if (respArray.filter { it.status == MODSTATUS.ACCEPTED }.size > 0) {
                            modStatusLiveData.postValue(MODSTATUS.ACCEPTED)
                        } else if (respArray.filter { it.status == MODSTATUS.REJECTED }.size == respArray.size) {
                            modStatusLiveData.postValue(MODSTATUS.REJECTED)
                        } else if (respArray.filter { it.status == MODSTATUS.BLOCKED }.size == respArray.size) {
                            modStatusLiveData.postValue(MODSTATUS.BLOCKED)
                        } else if (respArray.filter { it.status != MODSTATUS.REJECTED && it.status != MODSTATUS.BLOCKED }.size == respArray.filter { it.status == MODSTATUS.PENDING }.size) {
                            modStatusLiveData.postValue(MODSTATUS.PENDING)
                        }
                    }

                }
                updateResponseObserver(it)
            }
        }
    }

    fun manageInvitation() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId
            map["status"] = status
            val response = repo.manageInvitation(map)
            withContext(Dispatchers.IO) {
                response.collect {
//                    if (it is Resource.Success<*>) {
//                        courseLiveData.postValue(courseLiveData.value?.filter {
//                            it.courseId != courseId
//                        })
//                    }
                    updateResponseObserver(it)

                }

            }
        }
    }

    fun requestCount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.requestCount()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<RequestCountModel>
                    requestCountLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }
    }

    fun cancelReq(courseId: String) = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["courseId"] = courseId
        val response = repo.cancelReq(map)
        withContext(Dispatchers.IO) {
            response.collect {
//                if (it is Resource.Success<*>) {
//                    val data = it.value as BaseResponse<RequestCountModel>
//                    requestCountLiveData.postValue(data.resource)
//                }
                updateResponseObserver(it)
            }
        }
    }

    suspend fun getRequestResponse(hashMap: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        val response = repo.getRequestList(hashMap).cachedIn(viewModelScope).asFlow()
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }


            }
        courseLiveData.value = response.asLiveData().value
        return response.asLiveData()
    }

    fun onViewEvent(PagerViewEventsRequest: PagerViewEventsRequest) {
        modificationEvents.value += PagerViewEventsRequest
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION -> {


            }
        }


    }


    fun applyEvents(
        paging: PagingData<CourseData>,
        PagerViewEventsRequest: PagerViewEventsRequest,
    ): PagingData<CourseData> {
        return when (PagerViewEventsRequest) {
            is PagerViewEventsRequest.Remove -> {
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
//                            userLiked = PagerViewEventsRequest.pagerViewData.userLiked,
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
//                                userLiked = 1,
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
}


sealed class PagerViewEventsRequest {
    data class Edit(val pagerViewData: CourseData) : PagerViewEventsRequest()
    data class Remove(val pagerViewData: CourseData) : PagerViewEventsRequest()
    data class EditLike(val pagerViewData: CourseData) : PagerViewEventsRequest()
    data class EditDisLike(val pagerViewData: CourseData) : PagerViewEventsRequest()
    data class EditListenData(val pagerViewData: CourseData) : PagerViewEventsRequest()
    data class Buy(val courseId: Int) : PagerViewEventsRequest()


}


