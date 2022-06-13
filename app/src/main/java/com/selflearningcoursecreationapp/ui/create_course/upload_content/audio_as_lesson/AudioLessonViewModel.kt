package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AudioLessonViewModel(private val repo: UploadContentRepo) : BaseViewModel() {


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
        contentDuration: Int,
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
        lectureId: Int?,
        sectionId: Int?,
        courseId: Int?,
        text: String,
        contentId: String,
        audio: Int,
        milliSecond: Int,
    ) {
        docLiveData.value?.let {
            val errorId = it.isAudioValid()
            if (errorId == 0) {
                addPatchLecture(
                    sectionId = sectionId,
                    mediaTypeId = audio,
                    lectureTitle = text,
                    lectureId = lectureId!!,
                    courseId = courseId!!,
                    id = contentId,
                    contentDuration = milliSecond
                )
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }

    fun uploadContent(
        courseId: Int?,
        sectionId: Int?,
        lectureId: Int,
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