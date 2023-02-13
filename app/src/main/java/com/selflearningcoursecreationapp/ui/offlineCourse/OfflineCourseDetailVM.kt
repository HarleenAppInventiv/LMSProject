package com.selflearningcoursecreationapp.ui.offlineCourse

import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.db.AppDb

class OfflineCourseDetailVM(private var mDatabase: AppDb) : BaseViewModel() {
    var courseId: Int = 0
    override fun onApiRetry(apiCode: String) {

    }

//    fun getSections() {
//        updateResponseObserver(Resource.Loading())
//        viewModelScope.launch {
//            withContext(Dispatchers.IO)
//            {
//                val list = mDatabase.getDao()
//                    .getAllCourseSections(
//                        courseId,
//                        userProfile?.id ?: 0
//                    ) as ArrayList<OfflineSectionData>
//                updateResponseObserver(Resource.Success(list, ApiEndPoints.OFFLINE_COURSE_SECTIONS))
//            }
//        }
//    }
}