package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFilterVM(private var repo: HomeFilterRepo) : BaseViewModel() {

    var filterLiveData = MutableLiveData<FilterResponse>().apply {
        value = FilterResponse()
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_REVIEW_FILTERS ->
                getFilters(false)
            else -> getFilters()

        }
    }

    fun getFilters(fromHome: Boolean = true, courseId: Int = 123) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = if (fromHome) repo.getFilters() else repo.getRatingFilters(courseId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        filterLiveData.postValue((it.value as BaseResponse<FilterResponse>).resource)
                    }

                    updateResponseObserver(it)
                }
            }
        }
    }
}