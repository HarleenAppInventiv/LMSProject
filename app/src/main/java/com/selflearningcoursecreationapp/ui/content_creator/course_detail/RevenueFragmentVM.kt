package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import RevenueDataList
import RevenueDataModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RevenueFragmentVM(private val repo: RevenueFragmentRepo) : BaseViewModel() {

    var currentPage = 1
    private var totalPage = 5
    private var courseid = 0
    var revenueType = 0
    var isLastPage = false
    var revenueLiveData = MutableLiveData<RevenueDataModel>().apply {
        value = RevenueDataModel()
    }

    var revenueLiveList = MutableLiveData<ArrayList<RevenueDataList>>().apply {
        value = ArrayList()
    }

    fun reset() {
        currentPage = 1
        totalPage = 5
        isLastPage = false
    }


    fun getRevenueData(courseId: Int) = viewModelScope.launch(coroutineExceptionHandle)
    {

        courseid = courseId
        if (!isLastPage) {

            val map = HashMap<String, Any>()
            map["courseId"] = courseId
            map["RevenueType"] = revenueType
            map["pageNumber"] = currentPage
            map["pageSize"] = 5

            val response = repo.getRevenueList(map)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<RevenueDataModel>).resource?.let { resource ->
//                            totalPage = resource.totalCount ?: currentPage
                            if (resource.pageNumber == 1) {
                                currentPage = 1

                                revenueLiveList.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = revenueLiveList.value
                            list?.addAll(resource.revenueList ?: ArrayList())
                            if (!resource.revenueList.isNullOrEmpty())
                                revenueLiveData.postValue(resource)

                            isLastPage = resource.revenueList.isNullOrEmpty()




                            revenueLiveList.postValue(list)


                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_GET_REVENUE_LIST -> {
                getRevenueData(courseid)
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


