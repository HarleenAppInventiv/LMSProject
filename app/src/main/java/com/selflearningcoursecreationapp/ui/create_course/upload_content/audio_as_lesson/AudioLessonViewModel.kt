package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AudioLessonViewModel(private val repo: UploadContentRepo) : BaseViewModel() {
    var lectureId: Int? = null
    var courseId: Int? = null
    var model: SectionModel? = null
    var sectionId: Int? = null

    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()

    }


    fun addPatchLecture(

        lectureTitle: String,

        id: String,
        contentDuration: Int,
    ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId ?: 0
            map["sectionId"] = sectionId.toString()
            map["lectureId"] = lectureId ?: 0
            map["mediaTypeId"] = MEDIA_TYPE.AUDIO.toString()
            map["lectureTitle"] = lectureTitle
            map["lectureContentId"] = id
            map["contentDuration"] = contentDuration
            val response = repo.addPatchLecture(map)
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

        text: String,
        contentId: String,
        milliSecond: Int,
    ) {
        docLiveData.value?.let {
            val errorId = it.isAudioValid()
            if (errorId == 0) {
                addPatchLecture(

                    lectureTitle = text,

                    id = contentId,
                    contentDuration = milliSecond
                )
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }

    fun uploadContent(
        file: File,
        duration: Int,
    ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.contentUpload(
                courseId,
                sectionId,
                lectureId ?: 0,
                file,
                MEDIA_TYPE.AUDIO,
                duration
            )
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }


    fun getLectureDetail() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.getLectureDetail(lectureId ?: 0)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }
}