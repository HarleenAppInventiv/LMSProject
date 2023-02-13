package com.selflearningcoursecreationapp.ui.dashboard

import LearnerDashboardData
import LearnerDashboardDataList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.extensions.convertToUtc
import com.selflearningcoursecreationapp.extensions.getCurrentDate
import com.selflearningcoursecreationapp.extensions.getNextDay
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.dashboard.model.ActivityHoursModel
import com.selflearningcoursecreationapp.ui.dashboard.model.LearnerDashboardFilteredStatCountModel
import com.selflearningcoursecreationapp.ui.dashboard.model.LearnerDashboardStatCountModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonPayload
import com.selflearningcoursecreationapp.utils.DASHBOARD_FILTER_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LearnerDashboardVM(private val repo: LearnerDashboardRepo) : BaseViewModel() {

    var currentPage = 1
    private var totalPage = 2
    var courseType = 0
    var filterType = DASHBOARD_FILTER_TYPE.ALL
    var minDate = getCurrentDate()
    var maxDate = getCurrentDate()
    var selectedDay = getCurrentDate()

    val refreshData = MutableLiveData<Boolean>().apply {
        value = true
    }
    var courseLiveData = MutableLiveData<ArrayList<LearnerDashboardDataList>>().apply {
        value = ArrayList()
    }

    var requestCountLiveData = MutableLiveData<LearnerDashboardStatCountModel>().apply {
        value = LearnerDashboardStatCountModel()
    }

    var activityTracerLiveData = MutableLiveData<ActivityHoursModel>().apply {
        value = ActivityHoursModel()
    }

    fun reset() {
        currentPage = 1
        totalPage = 2
        courseLiveData.value?.clear()
        isLastPage = false
    }


    var isLastPage = false

    fun getCourses() = viewModelScope.launch(coroutineExceptionHandle)
    {
        var model = GetReviewsRequestModel()
        var filterFields = arrayListOf<SearchFieldsItem>()
        model.pageSize = 5
        model.pageNumber = currentPage
        model.courseType = courseType
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

            val response = repo.getEnrolledCourses(model)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<LearnerDashboardData>).resource?.let { resource ->
                            val totalPage = resource.totalCount / resource.pageSize
                            val remCount = resource.totalCount % resource.pageSize
                            this@LearnerDashboardVM.totalPage =
                                totalPage + (if (remCount > 0) 1 else 0)
//                            totalPage = resource.resultCount
                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                courseLiveData.postValue(ArrayList())
                            }
                            showLog("CURRENTPAGE BEFORE", "" + currentPage)
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


    var filteredRequestCountLiveData =
        MutableLiveData<LearnerDashboardFilteredStatCountModel>().apply {
            value = LearnerDashboardFilteredStatCountModel()
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
                    val data = it.value as BaseResponse<LearnerDashboardFilteredStatCountModel>
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
                    val data = it.value as BaseResponse<LearnerDashboardStatCountModel>
                    requestCountLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_LEARNER_DASHBOARD_COURSES_LIST -> {
                getCourses()
            }

            ApiEndPoints.API_LEARNER_DASHBOARD_STAT_COUNT -> {
                requestCount()
            }
            ApiEndPoints.API_LEARNER_DASHBOARD_FILTERED_COUNT -> {
                getFilteredCount()
            }

            ApiEndPoints.API_CREATOR_DASHBOARD_TOTAL_ACTIVITY_TIME -> {
                getActivityTrackTime()
            }

        }

    }


    fun getActivityTrackTime(isToday: Boolean = true, isAllSelected: Boolean = false) {

        viewModelScope.launch(coroutineExceptionHandle) {
            val response =  /* if (isToday){
               repo.getTrackUserActivity(getLastDay(getCurrentDate()), getCurrentDate())

            }else*/ if (isAllSelected) {
                repo.getTrackUserActivity(
                    "1800-01-01",
                    endDate = getNextDay(maxDate).convertToUtc()
                )

            } else {
                repo.getTrackUserActivity(/*getLastDay(*/minDate/*)*/.convertToUtc(),
                    endDate = getNextDay(maxDate).convertToUtc()
                )

            }

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<ActivityHoursModel>
                        activityTracerLiveData.postValue(data.resource)
                    }
                    updateResponseObserver(it)
                }
            }

        }
    }
}




