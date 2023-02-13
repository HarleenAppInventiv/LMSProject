package com.selflearningcoursecreationapp.ui.bottom_course

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.AllCoursesResponse
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCourseVM(private val repo: MyCoursesRepo) : BaseViewModel() {
    var adapterPosition: Int = 0
    var currentPage = 1
    private var totalPage = 2
    private var courseType = 0

    var viewPagerScroll = MutableLiveData<Boolean>().apply {
        value = true
    }
    var courseId: Int = 0
    var courseLiveData = MutableLiveData<ArrayList<CourseData>>().apply {
        value = ArrayList()
    }

    fun reset() {
        currentPage = 1
        totalPage = 2
        courseLiveData.value?.clear()
    }

    fun getCourses(type: Int) = viewModelScope.launch(coroutineExceptionHandle)
    {
        courseType = type
        if (currentPage < totalPage) {
            val map = HashMap<String, Any>()

            map.put("pageNumber", currentPage)
            map.put("courseType", type)

            val response = repo.onGoing(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<AllCoursesResponse>).resource?.let { resource ->
                            val totalPage = (resource?.totalCount ?: 0) / (resource?.pageSize ?: 8)
                            val remCount = (resource?.totalCount ?: 0) % (resource?.pageSize ?: 8)
                            this@MyCourseVM.totalPage =
                                totalPage + (if (remCount > 0) 1 else 0)

//                            totalPage = resource.resultCount ?: 2
                            if (resource.currentPage == 1) {
                                currentPage = 1
                                courseLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = courseLiveData.value
                            list?.addAll(resource.coursesList ?: ArrayList())
                            courseLiveData.postValue(list)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun editToDraft(courseId: Int) = viewModelScope.launch(coroutineExceptionHandle)
    {

        val response = repo.editCourseToDraft(courseId)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }

    fun deleteCourse() = viewModelScope.launch()
    {
        val response = repo.deleteCourse(courseId)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    courseLiveData.value?.let { list ->
                        list.removeAt(adapterPosition)
                    }
                }
                updateResponseObserver(it)

            }
        }

    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ALL_COURSES -> {
                getCourses(courseType)
            }
            ApiEndPoints.API_EDIT_TO_DRAFT -> {
                editToDraft(courseId)
            }
            ApiEndPoints.API_CRE_STEP_1 -> {
                deleteCourse()
            }

        }

    }


}