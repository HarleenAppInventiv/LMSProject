package com.selflearningcoursecreationapp.ui.moderator.moderatorHome.rejected

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

class ModRejectedVM(repo: ModHomeRepo) : ModHomeVM(repo) {

    suspend fun rejectCourse(): LiveData<PagingData<CourseData>> {
        val response = repo.modCourse(filterData).cachedIn(viewModelScope).asFlow()
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }


            }
        requestLiveData.value = response.asLiveData().value
        return response.asLiveData()
    }

    fun updateCourseStatus(status: Int, data: CourseData, comment: String? = "") {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["id"] = data.id ?: 0
            map["status"] = status ?: 0
            map["comment"] = comment ?: ""
            val response = repo.updateCourseStatus(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseData>).resource
                        onViewEvent(PagerViewEventsRequest.Remove(data))

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