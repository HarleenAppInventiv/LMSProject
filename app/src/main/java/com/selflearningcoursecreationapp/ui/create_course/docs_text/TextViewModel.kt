package com.selflearningcoursecreationapp.ui.create_course.docs_text

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextViewModel(private val repo: TextRepo) : BaseViewModel() {

    var textLiveData = MutableLiveData<SectionModel>().apply {
        value = SectionModel()
    }
    var courseData = MutableLiveData<CourseData>().apply {
        value = CourseData()

    }


    fun addPatchLecture(
        sectionId: Int?,
        mediaTypeId: Int?,
        lectureTitle: String,
        lectureId: Int,
        courseId: Int
    ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId.toString()
            map["sectionId"] = sectionId.toString()
            map["lectureId"] = lectureId
            map["mediaTypeId"] = mediaTypeId.toString()
            map["lectureTitle"] = lectureTitle
//            map["lectureContentId"] = ""
//            map["lectureThumbnailId"] = ""
            var response = repo.addPatchLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
//                val value = (it as BaseResponse<UserProfile>).resource?.sectionId
//                addSectionLiveData.value = if (value != 0) true else false

//                    if (it is Resource.Success<*>) {
//
//                        addPatchLectureLiveData.postValue((it.value as BaseResponse<UserProfile>).resource)
//
//                    }

                    updateResponseObserver(it)
                }
            }
        }

    fun textValidations(sectionId: Int, i: Int, content: String, lectureId: Int, courseId: Int) {
        textLiveData.value?.let {
            val errorId = it.isDocValid()
            if (errorId == 0) {
                addPatchLecture(sectionId, i, content, lectureId, courseId)

            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }

        }
    }
}