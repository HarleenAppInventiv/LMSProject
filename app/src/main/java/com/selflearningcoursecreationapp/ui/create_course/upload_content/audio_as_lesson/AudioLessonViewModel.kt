package com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.course.LessonArgs
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AudioLessonViewModel(private val repo: UploadContentRepo) : BaseViewModel() {

    var lessonArgs: LessonArgs? = null

    //    var lectureId: Int? = null
//    var courseId: Int? = null
//    var model: SectionModel? = null
//    var sectionId: Int? = null
    var contentId = ""
    var mimeType = ""
    var milliSecond = 0L
    var fileName = ""
    var fileSize = 0L
    private var uploadFile: File? = null

    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()

    }


    fun addLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = lessonArgs?.courseId ?: 0
            map["sectionId"] = lessonArgs?.sectionId?.toString() ?: ""
            map["mediaTypeId"] = MediaType.AUDIO.toString()
            val response = repo.addLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<ChildModel>).resource
                        lessonArgs?.lectureId = resource?.lectureId
                        uploadMetaData()
                    }
                    updateResponseObserver(it)
                }
            }
        }

    fun addPatchLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = lessonArgs?.courseId ?: 0
            map["sectionId"] = lessonArgs?.sectionId.toString()
            map["lectureId"] = lessonArgs?.lectureId ?: 0
            map["mediaTypeId"] = MediaType.AUDIO.toString()
            map["lectureTitle"] = docLiveData.value?.lectureTitle?.trim() ?: ""
            map["lectureContentId"] = contentId
            map["contentDuration"] = milliSecond
            val response = repo.addPatchLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }


    fun docValidations() {
        docLiveData.value?.let {
            val errorId = it.isAudioValid()
            if (errorId == 0) {
                if (lessonArgs?.lectureId == 0) {
                    addLecture()
                } else if (!fileName.isNullOrEmpty()) {
                    uploadMetaData()
                } else {
                    addPatchLecture()
                }
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }

    fun uploadMetaData(
    ) {

        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = lessonArgs?.courseId ?: 0
            map["sectionId"] = lessonArgs?.sectionId ?: 0
            map["lectureId"] = lessonArgs?.lectureId ?: 0
            map["fileName"] = fileName
            map["uploadType"] = MediaType.AUDIO
            map["contentDuration"] = milliSecond
            map["contentSize"] = fileSize
            map["mimeType"] = mimeType
            val response = repo.contentUploadMetaData(map)

            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }
    }

    fun uploadContent(
        file: File,
        duration: Long,
    ) {
        milliSecond = duration
        uploadFile = file
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.contentUpload(
                lessonArgs?.courseId,
                lessonArgs?.sectionId,
                lessonArgs?.lectureId ?: 0,
                file,
                MediaType.AUDIO,
                duration
            )
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }

    }


    fun getLectureDetail() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.getLectureDetail(lessonArgs?.lectureId ?: 0, lessonArgs?.courseId ?: 0)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_POST -> {
                docValidations()
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH + "/patch" -> {
                addPatchLecture()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                uploadFile?.let { uploadContent(it, milliSecond) }
            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                getLectureDetail()
            }
            ApiEndPoints.API_UPLOAD_METADATA -> {
                uploadMetaData()
            }
        }
    }
}