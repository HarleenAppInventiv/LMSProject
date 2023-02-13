package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import ModDashboardData
import ModDashboardDataList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.extensions.convertToUtc
import com.selflearningcoursecreationapp.extensions.getCurrentDate
import com.selflearningcoursecreationapp.extensions.getNextDay
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.moderator.model.ModDashboardStatCountModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonPayload
import com.selflearningcoursecreationapp.utils.DASHBOARD_FILTER_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ModDashboardVM(private val repo: ModDashboardRepo) : BaseViewModel() {

    var currentPage = 1
    private var totalPage = 2
    var moderatorStatus = 0
    var filterType = DASHBOARD_FILTER_TYPE.ALL
    var isLastPage = false
    var minDate = getCurrentDate()
    var maxDate = getCurrentDate()
    var selectedDay = getCurrentDate()
    var courseLiveData = MutableLiveData<ArrayList<ModDashboardDataList>>().apply {
        value = ArrayList()
    }
    val refreshData = MutableLiveData<Boolean>().apply {
        value = true
    }

    var requestCountLiveData = MutableLiveData<ModDashboardStatCountModel>().apply {
        value = ModDashboardStatCountModel()
    }

    var filteredRequestCountLiveData = MutableLiveData<ModDashboardStatCountModel>().apply {
        value = ModDashboardStatCountModel()
    }

    fun reset() {
        currentPage = 1
        totalPage = 5
        courseLiveData.value?.clear()
        isLastPage = false
    }


    fun getCourses() = viewModelScope.launch(coroutineExceptionHandle)
    {

        var model = GetReviewsRequestModel()
        var filterFields = arrayListOf<SearchFieldsItem>()
        model.pageSize = 5
        model.pageNumber = currentPage
        model.moderatorStatus = moderatorStatus
        if (!isLastPage) {


            if (filterType == DASHBOARD_FILTER_TYPE.DAY) {

                filterFields.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_6,
                        arrayListOf("'${selectedDay.convertToUtc()}'")
                    )
                )

                filterFields.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_7,
                        arrayListOf("'${getNextDay(selectedDay).convertToUtc()}'")
                    )
                )

                model.searchFields = filterFields

            }
            if (filterType == DASHBOARD_FILTER_TYPE.WEEK || filterType == DASHBOARD_FILTER_TYPE.MONTH) {

                filterFields.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_6,
                        arrayListOf("'${minDate.convertToUtc()}'")
                    )
                )

                filterFields.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_7,
                        arrayListOf("'${getNextDay(maxDate).convertToUtc()}'")
                    )
                )

                model.searchFields = filterFields

            }

            val response = repo.acceptedCourses(model)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<ModDashboardData>).resource?.let { resource ->
                            val totalPage = (resource?.totalCount ?: 0) / (resource?.pageSize ?: 8)
                            val remCount = (resource?.totalCount ?: 0) % (resource?.pageSize ?: 8)
                            this@ModDashboardVM.totalPage =
                                totalPage + (if (remCount > 0) 1 else 0)
//                            totalPage = resource.resultCount
                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                courseLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = courseLiveData.value
                            list?.addAll(resource.list)
//                            if (resource.list.isNullOrEmpty())
                            isLastPage = resource.list.isNullOrEmpty()
                            courseLiveData.postValue(list)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun getFilteredCount() = viewModelScope.launch(coroutineExceptionHandle)
    {


        var model = GetReviewsRequestModel()
        var filterFields = arrayListOf<SearchFieldsItem>()
        model.pageSize = 5
        model.pageNumber = currentPage

        if (filterType == DASHBOARD_FILTER_TYPE.DAY) {
            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_6,
                    arrayListOf("'${selectedDay.convertToUtc()}'")
                )
            )

            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_7,
                    arrayListOf("'${getNextDay(selectedDay).convertToUtc()}'")
                )
            )

            model.searchFields = filterFields
        }
        if (filterType == DASHBOARD_FILTER_TYPE.WEEK || filterType == DASHBOARD_FILTER_TYPE.MONTH) {
            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_6,
                    arrayListOf("'${minDate.convertToUtc()}'")
                )
            )

            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_7,
                    arrayListOf("'${getNextDay(maxDate).convertToUtc()}'")
                )
            )

            model.searchFields = filterFields
        }


        val response = repo.getFilteredCount(model)

        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<ModDashboardStatCountModel>
                    filteredRequestCountLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }

    }


    fun requestCount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.courseStatCount()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<ModDashboardStatCountModel>
                    requestCountLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_MOD_DASHBOARD_COURSES_LIST -> {
                getCourses()
            }

            ApiEndPoints.API_MOD_DASHBOARD_STAT_COUNT -> {
                requestCount()
            }

            ApiEndPoints.API_MOD_DASHBOARD_FILTERED_COUNT -> {
                getFilteredCount()
            }

        }

    }


}



