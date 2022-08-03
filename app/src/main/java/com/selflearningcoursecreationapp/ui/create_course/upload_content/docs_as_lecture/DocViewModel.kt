package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DocViewModel(private val repo: UploadContentRepo) : BaseViewModel() {

    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()
    }
    var lectureId: Int? = null
    var courseId: Int? = null
    var model: SectionModel? = null
    var contentId: String = ""
    var uri = ""

    private fun addPatchLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId ?: 0
            map["sectionId"] = model?.sectionId.toString()
            map["lectureId"] = lectureId ?: -1
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
                addPatchLecture()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }

    fun uploadContent() {
        val file = File(Uri.parse(uri).path ?: "")
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.contentUpload(
                courseId,
                model?.sectionId,
                lectureId ?: -1,
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

        val response = repo.getLectureDetail(lectureId ?: 0)
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
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                docValidations()
            }
        }
    }
}