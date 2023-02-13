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

class OfflineCourseVM(private var mDatabase: AppDb) : BaseViewModel() {

    override fun onApiRetry(apiCode: String) {

    }

    fun getAllOfflineCourses() {
        updateResponseObserver(Resource.Loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                val list = mDatabase.getDao()
                    .getAllCourses(userProfile?.id ?: 0) as ArrayList<OfflineCourseData>
                updateResponseObserver(Resource.Success(list, ApiEndPoints.ALL_OFFLINE_COURSES))
            }
        }
    }

    fun getOfflineCourse(courseId: Int) {
        updateResponseObserver(Resource.Loading())
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                val id = mDatabase.getDao().getCourse(courseId, userProfile?.id ?: 0)
                updateResponseObserver(Resource.Success(id, ApiEndPoints.OFFLINE_COURSE))
            }
        }
    }
}