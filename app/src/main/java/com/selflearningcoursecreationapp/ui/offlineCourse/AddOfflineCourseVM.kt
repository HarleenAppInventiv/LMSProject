package com.selflearningcoursecreationapp.ui.offlineCourse

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.db.AppDb
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddOfflineCourseVM(private var mDatabase: AppDb) : BaseViewModel() {

    override fun onApiRetry(apiCode: String) {

    }

    fun insertOfflineCourse(course: OfflineCourseData) {
        updateResponseObserver(Resource.Loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                val id = mDatabase.getDao().insertCourseData(course)
                updateResponseObserver(Resource.Success(id, ApiEndPoints.ADD_OFFLINE_COURSE))
            }
        }
    }

    fun updateOfflineCourse(course: OfflineCourseData) {
        updateResponseObserver(Resource.Loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                val id = mDatabase.getDao().updateCourse(course)
                updateResponseObserver(Resource.Success(id, ApiEndPoints.UPDATE_OFFLINE_COURSE))
            }
        }
    }

    fun deleteCourse(course: OfflineCourseData) {
        updateResponseObserver(Resource.Loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                val id = mDatabase.getDao().deleteCourse(course)
                updateResponseObserver(Resource.Success(id, ApiEndPoints.DELETE_OFFLINE_COURSE))
            }
        }
    }
}