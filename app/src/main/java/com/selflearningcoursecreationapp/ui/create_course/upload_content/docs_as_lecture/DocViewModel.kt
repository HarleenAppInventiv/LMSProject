package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture

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

class DocViewModel(private val repo: UploadContentRepo) : BaseViewModel() {

    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()
    }
    var lectureId: Int? = null
    var courseId: Int? = null
    var model: SectionModel? = null

    fun addPatchLecture(

        lectureTitle: String,
        id: String,
        contentDuration: Long,

        ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId ?: 0
            map["sectionId"] = model?.sectionId.toString()
            map["lectureId"] = lectureId ?: -1
            map["mediaTypeId"] = MEDIA_TYPE.DOC.toString()
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
        content: String,
        id: String,
        contentDuration: Long,
    ) {
        docLiveData.value?.let {
            val errorId = it.isDocValid()
            if (errorId == 0) {
                addPatchLecture(content, id, contentDuration)
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
                model?.sectionId,
                lectureId ?: -1,
                file,
                MEDIA_TYPE.DOC,
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