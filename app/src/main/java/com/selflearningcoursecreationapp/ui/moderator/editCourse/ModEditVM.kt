package com.selflearningcoursecreationapp.ui.moderator.editCourse

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.models.course.CourseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModEditVM(private val repo: ModEditRepo) : BaseViewModel() {
    val courseData = MutableLiveData<CourseData>()

    fun editCourse(content: String) {
        val map = HashMap<String, Any>()
        map["courseId"] = courseData.value?.courseId ?: 0
        map["courseTitle"] = content
        map["courseDescription"] = courseData.value?.courseDescription ?: ""
        map["categoryId"] = courseData.value?.categoryId ?: 0
        map["languageId"] = courseData.value?.languageId ?: 0

        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.editCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
//                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as BaseResponse<CourseData>).resource
//                        courseData.postValue(resource)
//                    }
                    updateResponseObserver(it)
                }
            }
        }

    }

    override fun onApiRetry(apiCode: String) {

    }
}