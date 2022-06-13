package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextViewModel(private val repo: UploadContentRepo) : BaseViewModel() {

    var textLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()
    }
    var courseData = MutableLiveData<CourseData>().apply {
        value = CourseData()

    }


    fun addPatchLecture(
        sectionId: Int?,
        mediaTypeId: Int?,
        lectureTitle: String,
        lectureId: Int,
        courseId: Int,
        lectureContentId: String,
        contentDuration: Long,

        ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId.toString()
            map["sectionId"] = sectionId.toString()
            map["lectureId"] = lectureId
            map["mediaTypeId"] = mediaTypeId.toString()
            map["lectureTitle"] = lectureTitle
            map["lectureContentId"] = lectureContentId
            map["contentDuration"] = contentDuration

            val response = repo.addPatchLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }

    fun textValidations(
        sectionId: Int,
        i: Int,
        content: String,
        lectureId: Int,
        courseId: Int,
        lectureContentId: String,
        contentDuration: Long,

        ) {
        textLiveData.value?.let {
            val errorId = it.isTextValid()
            if (errorId == 0) {
                addPatchLecture(
                    sectionId,
                    i,
                    content,
                    lectureId,
                    courseId,
                    lectureContentId,
                    contentDuration
                )

            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }

        }
    }


    fun getLectureDetail(lectureId: Int) = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repo.getLectureDetail(lectureId)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }

    fun uploadContent(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        uploadType: Int,
        text: String,
        duration: Int,
    ) = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.contentUploadText(
            courseId,
            sectionId,
            lectureId,
            uploadType,
            text,
            duration
        )
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }
}