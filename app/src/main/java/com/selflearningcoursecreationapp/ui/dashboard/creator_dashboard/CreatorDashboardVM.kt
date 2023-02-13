package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard

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
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorAudienceStateCount
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCount
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCountWithFilter
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorTotalEarnings
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonPayload
import com.selflearningcoursecreationapp.utils.DASHBOARD_FILTER_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreatorDashboardVM(private val repo: CreatorDashboardRepo) : BaseViewModel() {

    var currentPage = 1
    private var totalPage = 2
    var filterType = DASHBOARD_FILTER_TYPE.ALL
    var earningFilterType = DASHBOARD_FILTER_TYPE.MONTH
    var minDate = getCurrentDate()
    var maxDate = getCurrentDate()
    var selectedDay = getCurrentDate()
    var earningSelectedDay = getCurrentDate()
    var earningMinDate = getCurrentDate()
    var earningMaxDate = getCurrentDate()

    init {
        getUserData()
    }

    var filteredCourseUserLiveData = MutableLiveData<CreatorCourseUserCountWithFilter>().apply {
        value = CreatorCourseUserCountWithFilter()
    }

    var audienceCountLiveData = MutableLiveData<CreatorAudienceStateCount>().apply {
        value = CreatorAudienceStateCount()
    }

    var courseUserCountLiveData = MutableLiveData<CreatorCourseUserCount>().apply {
        value = CreatorCourseUserCount()
    }

    var totalEarningLiveData = MutableLiveData<CreatorTotalEarnings>().apply {
        value = CreatorTotalEarnings()
    }

    var ageProfessionFilterData = GetReviewsRequestModel()

    fun reset() {
        currentPage = 1
        totalPage = 2
//        filteredCourseUserLiveData.value?.list.clear()
        isLastPage = false
    }


    var currentDate: String = ""
    var isLastPage = false


    fun getCourseUserCount() = viewModelScope.launch(coroutineExceptionHandle)
    {
        var model = GetReviewsRequestModel()
        var filterFields = arrayListOf<SearchFieldsItem>()
        model.pageSize = 5
        model.pageNumber = currentPage

        if (!ageProfessionFilterData.searchFields.isNullOrEmpty()) {
            filterFields.addAll(ageProfessionFilterData.searchFields as ArrayList<SearchFieldsItem>)
        }

        if (filterType == 0) {

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

        }
        if (filterType == 1 || filterType == 2) {

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

        }
        model.searchFields = filterFields


        val response = repo.courseUserCount(model)


        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<CreatorCourseUserCount>
                        courseUserCountLiveData.postValue(data.resource)
                    }
                }
                updateResponseObserver(it)
            }


        }
    }

    fun getFilteredCourseUsersCount() = viewModelScope.launch(coroutineExceptionHandle)
    {
        var modelFilteredData = GetReviewsRequestModel()
        var filterFieldsForFilteredData = arrayListOf<SearchFieldsItem>()

        if (!isLastPage) {
            modelFilteredData.pageSize = 5
            modelFilteredData.pageNumber = currentPage

            if (!ageProfessionFilterData.searchFields.isNullOrEmpty()) {
                filterFieldsForFilteredData.addAll(ageProfessionFilterData.searchFields as ArrayList<SearchFieldsItem>)
            }

            if (filterType == DASHBOARD_FILTER_TYPE.DAY) {

                filterFieldsForFilteredData.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_6,
                        arrayListOf("'${selectedDay.convertToUtc()}'")
                    )
                )

                filterFieldsForFilteredData.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_7,
                        arrayListOf("'${getNextDay(selectedDay).convertToUtc()}'")
                    )
                )

            }
            if (filterType == DASHBOARD_FILTER_TYPE.WEEK || filterType == DASHBOARD_FILTER_TYPE.MONTH) {

                filterFieldsForFilteredData.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_6,
                        arrayListOf("'${minDate.convertToUtc()}'")
                    )
                )

                filterFieldsForFilteredData.add(
                    SearchFieldsItem(
                        CommonPayload.MODIFIED_DATE,
                        CommonPayload.OPERATOR_TYPE_7,
                        arrayListOf("'${getNextDay(maxDate).convertToUtc()}'")
                    )
                )

            }
            modelFilteredData.searchFields = filterFieldsForFilteredData


            val response = repo.filteredCourseUserCount(modelFilteredData)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<CreatorCourseUserCountWithFilter>).resource?.let { resource ->
                            totalPage = resource.resultCount ?: 2
                            if (currentPage == 1) {
                                filteredCourseUserLiveData.postValue(resource)
                            }
                            currentPage += 1
                            val list = filteredCourseUserLiveData.value?.list
                            resource.list?.let { it1 -> list?.addAll(it1) }
                            if (resource.list.isNullOrEmpty())
                                isLastPage = true
                            filteredCourseUserLiveData.postValue(resource)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun audienceStateCount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.audienceStatCount()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<CreatorAudienceStateCount>
                    audienceCountLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }
    }

    fun totalEarnings() = viewModelScope.launch(coroutineExceptionHandle)
    {

        var model = GetReviewsRequestModel()
        var filterFields = arrayListOf<SearchFieldsItem>()

        if (!ageProfessionFilterData.searchFields.isNullOrEmpty()) {
            filterFields.addAll(ageProfessionFilterData.searchFields as ArrayList<SearchFieldsItem>)
        }

        if (earningFilterType == DASHBOARD_FILTER_TYPE.DAY) {

            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_6,
                    arrayListOf("'${earningSelectedDay.convertToUtc()}'")
                )
            )

            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_7,
                    arrayListOf("'${getNextDay(earningSelectedDay).convertToUtc()}'")
                )
            )

        }
        if (earningFilterType == DASHBOARD_FILTER_TYPE.WEEK || earningFilterType == DASHBOARD_FILTER_TYPE.MONTH) {

            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_6,
                    arrayListOf("'${earningMinDate.convertToUtc()}'")
                )
            )

            filterFields.add(
                SearchFieldsItem(
                    CommonPayload.MODIFIED_DATE,
                    CommonPayload.OPERATOR_TYPE_7,
                    arrayListOf("'${getNextDay(earningMaxDate).convertToUtc()}'")
                )
            )


        }


        model.searchFields = filterFields
        val response = repo.creatorDashTotalEarning(model)


        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<CreatorTotalEarnings>
                        totalEarningLiveData.postValue(data.resource)
                    }
                }
                updateResponseObserver(it)
            }


        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_CREATOR_DASHBOARD_AUDIENCE_STAT_COUNT -> {
                audienceStateCount()
            }

            ApiEndPoints.API_CREATOR_DASHBOARD_TOTAL_EARNING -> {
                totalEarnings()
            }
            ApiEndPoints.API_CREATOR_DASHBOARD_COURSE_USER_COUNT_FILTERED -> {
                getFilteredCourseUsersCount()
            }
            ApiEndPoints.API_CREATOR_DASHBOARD_COURSE_USER_COUNT -> {
                getCourseUserCount()
            }

        }

    }


}




