package com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.upload_content.UploadContentRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VideoLessonViewModel(private val repo: UploadContentRepo) : BaseViewModel() {
    var docLiveData = MutableLiveData<ChildModel>().apply {
        value = ChildModel()

    }

    var videoLiveData = MutableLiveData<VideoModel>().apply {
        value = VideoModel()

    }
    var mThumbId = ""
    var mDuration = 0L
    var thumbUri: String? = null

    var mContentId = ""

    private fun addPatchLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = videoLiveData.value?.mCourseId ?: 0
            map["sectionId"] = videoLiveData.value?.mSectionId.toString()
            map["lectureId"] = videoLiveData.value?.mLectureId ?: 0
            map["mediaTypeId"] = MediaType.VIDEO.toString()
            map["lectureTitle"] = docLiveData.value?.lectureTitle?.trim() ?: ""
            map["lectureContentId"] = mContentId
            map["lectureThumbnailId"] =
                if (mThumbId.isNullOrEmpty()) docLiveData.value?.lectureThumbnailId
                    ?: "" else mThumbId
            map["contentDuration"] = mDuration?.toString()
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
                addPatchLecture()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }

    fun uploadContent() {
        val file = File(Uri.parse(videoLiveData.value?.mFilePath).path ?: "")
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.contentUpload(
                videoLiveData.value?.mCourseId,
                videoLiveData.value?.mSectionId,
                videoLiveData.value?.mLectureId ?: 0,
                file,
                MediaType.VIDEO,
                mDuration
            )
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }

    }

    fun getLectureDetail() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.getLectureDetail(videoLiveData.value?.mLectureId ?: 0)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }


    fun uploadThumbnail() {

        val file = File(Uri.parse(thumbUri).path ?: "")
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.thumbnailUpload(
                videoLiveData.value?.mCourseId,
                videoLiveData.value?.mSectionId,
                videoLiveData.value?.mLectureId ?: 0,
                file,
            )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<ImageResponse>).resource?.let { resource ->
                            mThumbId = resource.id.toString()


                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                docValidations()
            }
            ApiEndPoints.API_THUMBNAIL_UPLOAD -> {
                uploadThumbnail()
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