package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture

import android.net.Uri
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

class DocViewModel(private val repo: UploadContentRepo) : BaseViewModel() {
    var lessonArgs: LessonArgs? = null


    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()
    }

    //    var lectureId: Int? = null
//    var courseId: Int? = null
//    var model: SectionModel? = null
    var contentId: String = ""
    var uri = ""
    var uri_tus = ""
    var fileName = ""
    var fileSize = 0L
    var mimeType = ""

    fun addPatchLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = lessonArgs?.courseId ?: 0
            map["sectionId"] = lessonArgs?.sectionId.toString()
            map["lectureId"] = lessonArgs?.lectureId ?: -1
            map["mediaTypeId"] = MediaType.DOC.toString()
            map["lectureTitle"] = docLiveData.value?.lectureTitle?.trim() ?: ""
            map["lectureContentId"] = contentId
            map["contentDuration"] = docLiveData.value?.lectureContentDuration ?: 0L
            val response = repo.addPatchLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }

    fun notifyData() {
        docLiveData.value?.notifyChange()
    }

    fun docValidations() {
        docLiveData.value?.let {
            val errorId = it.isDocValid()
            if (errorId == 0) {
                if (lessonArgs?.lectureId == 0) {
                    addLecture()
                } else if (!uri_tus.isNullOrEmpty()) {
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
            map["uploadType"] = MediaType.DOC
            map["contentDuration"] = 0L
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

    fun uploadContent() {
        val file = File(Uri.parse(uri).path ?: "")
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.contentUpload(
                lessonArgs?.courseId,
                lessonArgs?.sectionId,
                lessonArgs?.lectureId ?: -1,
                file,
                MediaType.DOC,
                0L
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
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                getLectureDetail()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                uploadContent()
            }
            ApiEndPoints.API_ADD_LECTURE_PATCH + "/patch" -> {
                addPatchLecture()
            }
            ApiEndPoints.API_ADD_LECTURE_POST -> {
                docValidations()
            }
            ApiEndPoints.API_UPLOAD_METADATA -> {
                uploadMetaData()
            }
        }
    }


    fun addLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = lessonArgs?.courseId ?: 0
            map["sectionId"] = lessonArgs?.sectionId?.toString() ?: ""
            map["mediaTypeId"] = MediaType.DOC.toString()
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
}