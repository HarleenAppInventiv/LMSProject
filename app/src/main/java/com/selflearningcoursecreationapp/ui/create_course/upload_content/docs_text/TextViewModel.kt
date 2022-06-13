package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
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
    var model: SectionModel? = null
    var lectureId: Int? = null
    var courseId: Int? = null


    fun addPatchLecture(
        lectureTitle: String,
        lectureContentId: String,
        contentDuration: Long,

        ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId.toString()
            map["sectionId"] = model?.sectionId.toString()
            map["lectureId"] = lectureId ?: -1
            map["mediaTypeId"] = MEDIA_TYPE.TEXT.toString()
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
        content: String,
        lectureContentId: String,
        contentDuration: Long,

        ) {
        textLiveData.value?.let {
            val errorId = it.isTextValid()
            if (errorId == 0) {
                addPatchLecture(
                    content,
                    lectureContentId,
                    contentDuration
                )

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

    fun uploadContent(
        text: String,
        duration: Int,
    ) = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.contentUploadText(
            courseId,
            model?.sectionId,
            lectureId ?: -1,
            MEDIA_TYPE.TEXT,
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