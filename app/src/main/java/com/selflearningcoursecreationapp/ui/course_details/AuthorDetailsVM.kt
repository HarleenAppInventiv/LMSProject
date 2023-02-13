package com.selflearningcoursecreationapp.ui.course_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.authorDetail.AuthorDetailsFragmentRepo
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorProfileCourses
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AuthorDetailsVM(private val repo: AuthorDetailsFragmentRepo) : BaseViewModel() {

    var authorUserId = 0
    var authorDetailLiveData = MutableLiveData<AuthorDetailsData>().apply {
        value = AuthorDetailsData()
    }

    var authorProfileCoursesLiveData = MutableLiveData<ArrayList<AuthorProfileCourses>>().apply {
        value = ArrayList()
    }

    init {
        getUserData()
    }

    var wishlistLiveData = MutableLiveData<EventObserver<Pair<Int?, Boolean>>>().apply {

    }


    fun reset() {
        currentPage = 1
        authorProfileCoursesLiveData.value?.clear()
        isLastPage = false
    }

    fun addWishlist() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

//            val coursePair =
//                authorDetailLiveData.value?.authorProfileCourses?.get(adapterPosition)?.let {
//                    Pair(it.courseId, it.courseWishlisted == 0)
//                }

            val coursePair =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.let {
                    Pair(it.courseId, it.courseWishlisted == 0)
                }




            map["courseId"] = coursePair?.first.toString()
            map["addToWishlist"] = coursePair?.second ?: true
            val response = repo.addWishlist(map, false)
            withContext(Dispatchers.IO) {
                response.collect {

//                    otherCourseLiveData.value?.forEach { data ->
//                        if (data.courseId == coursePair?.first) {
//                            data.bookmarkProgress = it is Resource.Loading
//                            data.notifyPropertyChanged(BR.bookmarkProgress)
//                            data.notifyChange()
//                        }
//
//                    }


                    if (it is Resource.Success<*>) {


                        val resource = (it.value as BaseResponse<UserProfile>).resource
//                        if (authorDetailLiveData.value?.authorProfileCourses?.get(adapterPosition)?.courseId == coursePair?.first) {
//                            authorDetailLiveData.value?.authorProfileCourses?.get(adapterPosition)?.courseWishlisted=
//                                if (coursePair?.second == true) 1 else 0
//
//                            wishlistLiveData.postValue(EventObserver(Pair(coursePair?.first, coursePair?.second)) as EventObserver<Pair<Int?, Boolean>>?)
//
//                            updateResponseObserver(
//                                Resource.Success(
//                                    coursePair,
//                                    ApiEndPoints.API_HOME_WISHLIST
//                                )
//                            )
//                        } else {
//                            updateResponseObserver(it)
//                        }
                        if (authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseId == coursePair?.first) {
                            authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseWishlisted =
                                if (coursePair?.second == true) 1 else 0

                            wishlistLiveData.postValue(
                                EventObserver(
                                    Pair(
                                        coursePair?.first,
                                        coursePair?.second
                                    )
                                ) as EventObserver<Pair<Int?, Boolean>>?
                            )

                            updateResponseObserver(
                                Resource.Success(
                                    coursePair,
                                    ApiEndPoints.API_HOME_WISHLIST
                                )
                            )
                        } else {
                            updateResponseObserver(it)
                        }
                    }
                }
            }
        }

    var isLastPage = false
    var currentPage = 1
    fun getAuthorDetails() = viewModelScope.launch(coroutineExceptionHandle)
    {

        var map = HashMap<String, Any>()
        map["pageSize"] = 5
        map["pageNumber"] = currentPage
        map["userId"] = authorUserId

        if (!isLastPage) {
            val response = repo.getAuthorDetails(map)

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<AuthorDetailsData>
                        authorDetailLiveData.postValue(data.resource)


                        (it.value as BaseResponse<AuthorDetailsData>).resource?.let { resource ->
                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                authorProfileCoursesLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = authorProfileCoursesLiveData.value
                            resource.authorProfileCourses?.let { it1 -> list?.addAll(it1) }
//                            if (resource.authorProfileCourses.isNullOrEmpty())
                            isLastPage = resource.authorProfileCourses.isNullOrEmpty()
                            authorDetailLiveData.value?.authorProfileCourses = list
                            authorProfileCoursesLiveData.postValue(list)

                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }


    }

    fun getCoAuthorDetails() = viewModelScope.launch(coroutineExceptionHandle)
    {
        var map = HashMap<String, Any>()
        map["pageSize"] = 5
        map["pageNumber"] = currentPage
        map["userId"] = authorUserId

        if (!isLastPage) {
            val response = repo.getCoAuthorDetails(map)

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<AuthorDetailsData>
                        authorDetailLiveData.postValue(data.resource)


                        (it.value as BaseResponse<AuthorDetailsData>).resource?.let { resource ->
                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                authorProfileCoursesLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = authorProfileCoursesLiveData.value
                            resource.authorProfileCourses?.let { it1 -> list?.addAll(it1) }
//                            if (resource.authorProfileCourses.isNullOrEmpty())
                            isLastPage = resource.authorProfileCourses.isNullOrEmpty()
                            authorDetailLiveData.value?.authorProfileCourses = list
                            authorProfileCoursesLiveData.postValue(list)

                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }


    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_COURSE_AUTHOR_DETAIL -> {
                getAuthorDetails()
            }

            ApiEndPoints.API_COURSE_COAUTHOR_DETAIL -> {
                getCoAuthorDetails()
            }

            ApiEndPoints.API_PURCHASE_COURSE -> {
                purchaseCourse()
            }

            ApiEndPoints.API_RAZORPAY_COURSE -> {
                buyRazorPayCourse()
            }

            ApiEndPoints.API_WISHLIST -> {
                addWishlist()
            }

        }

    }

    var adapterPosition = 0
    var purchaseCourseLiveData = MutableLiveData<EventObserver<OrderData?>>()

    fun purchaseCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseId ?: 0
            map["courseType"] =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseTypeId ?: 0
            val response = repo.purchaseCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<OrderData>).resource
                        purchaseCourseLiveData.postValue(EventObserver(resource))

                    }
                    updateResponseObserver(it)
                }
            }
        }

    fun purchaseCoAuthorCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseId
                    ?: 0
            map["courseType"] =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseTypeId
                    ?: 0
            val response = repo.purchaseCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<OrderData>).resource
                        purchaseCourseLiveData.postValue(EventObserver(resource))

                    }
                    updateResponseObserver(it)
                }
            }
        }

    var stateId = ""
    fun buyRazorPayCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()


            map["courseId"] =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseId ?: 0
            map["amount"] =
                authorProfileCoursesLiveData.value?.get(adapterPosition)?.courseFee ?: ""
            map["currency"] = "INR"
            map["stateId"] = stateId


            val response = repo.buyRazorPayCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
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


