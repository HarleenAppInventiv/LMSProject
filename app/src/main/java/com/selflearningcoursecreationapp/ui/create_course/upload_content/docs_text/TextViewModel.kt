package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextViewModel(private val repo: UploadContentRepo) : BaseViewModel() {

    var textLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()
    }
    var courseData = MutableLiveData<CourseData>().apply {
        value = CourseData()

    }
    var model: SectionModel? = null
    var lectureId: Int? = null
    var courseId: Int? = null
    var lectureContentId: String? = ""


    private fun addPatchLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId.toString()
            map["sectionId"] = model?.sectionId.toString()
            map["lectureId"] = lectureId ?: -1
            map["mediaTypeId"] = MediaType.TEXT.toString()
            map["lectureTitle"] = textLiveData.value?.lectureTitle?.trim() ?: ""
            map["lectureContentId"] = lectureContentId ?: ""
            map["contentDuration"] = textLiveData.value?.lectureContentDuration ?: ""

            val response = repo.addPatchLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }

    fun textValidations() {
        textLiveData.value?.let {
            val errorId = it.isTextValid()
            if (errorId == 0) {
                addPatchLecture()

            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }

        }
    }


    fun getLectureDetail() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repo.getLectureDetail(lectureId ?: -1)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }

    fun uploadContent() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.contentUploadText(
            courseId,
            model?.sectionId,
            lectureId ?: -1,
            MediaType.TEXT,
            textLiveData.value?.textFileText ?: "",
            0
        )
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    (it.value as BaseResponse<ImageResponse>).resource?.let { resource ->
                        lectureContentId = resource.id.toString()
                    }
                }
                updateResponseObserver(it)
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                textValidations()
            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                getLectureDetail()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                uploadContent()
            }
        }
    }
}