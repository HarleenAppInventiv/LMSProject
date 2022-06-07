package com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DocViewModel(private val repo: DocRepo) : BaseViewModel() {

    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()
    }

    fun addPatchLecture(
        sectionId: Int?,
        mediaTypeId: Int?,
        lectureTitle: String,
        lectureId: Int,
        courseId: Int,
        id: String,
        contentDuration: Long,

        ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId
            map["sectionId"] = sectionId.toString()
            map["lectureId"] = lectureId
            map["mediaTypeId"] = mediaTypeId.toString()
            map["lectureTitle"] = lectureTitle
            map["lectureContentId"] = id
            map["contentDuration"] = contentDuration
            var response = repo.addPatchLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }

    fun notifyData() {
//        courseData.value = courseData.value
        docLiveData.value?.notifyChange()
    }

    fun docValidations(
        sectionId: Int,
        i: Int,
        content: String,
        lectureId: Int,
        courseId: Int,
        id: String,
        contentDuration: Long,
    ) {
        docLiveData.value?.let {
            val errorId = it.isDocValid()
            if (errorId == 0) {
                addPatchLecture(sectionId, i, content, lectureId, courseId, id, contentDuration)
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }

    fun uploadContent(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
        fileName: String,
        file: File,
        uploadType: Int,
        text: String,
        duration: Int,
    ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.contentUpload(
                courseId,
                sectionId,
                lectureId,
                fileName,
                file,
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


    fun getLectureDetail(lectureId: Int) = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repo.getLectureDetail(lectureId)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }
}