package com.selflearningcoursecreationapp.ui.moderator.moderatorHome.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeRepo
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeVM
import com.selflearningcoursecreationapp.ui.profile.requestTracker.PagerViewEventsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.set

class ModRequestVM(repo: ModHomeRepo) : ModHomeVM(repo) {

    suspend fun courseRequest(): LiveData<PagingData<CourseData>> {
        val response = repo.courseRequest(filterData).cachedIn(viewModelScope).asFlow()
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }
            }
        requestLiveData.value = response.asLiveData().value
        return response.asLiveData()
    }

    fun updateRequestStatus(status: Int, data: CourseData) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Int>()
            map["id"] = data.id ?: 0
            map["status"] = status ?: 0
            val response = repo.updateCourseRequest(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseData>).resource
                        onViewEvent(PagerViewEventsRequest.Remove(data))
                        switchTab.postValue(true)
                    }
                    updateResponseObserver(it)
                }
            }
        }

    }


    override fun onApiRetry(apiCode: String) {
        super.onApiRetry(apiCode)

    }
}