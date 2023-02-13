package com.selflearningcoursecreationapp.ui.course_details.video

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailRepo
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MODTYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentDetailViewModel(private var repo: CourseDetailRepo) : BaseViewModel() {

    var courseId: Int = 0
    var lectureId: Int = 0
    var status: Int = 0
    var modType: Int = 0
    var sectionId: Int = 0
    var duration: Int = 0
    var mediaType: Int = 0


    var lectureLiveData = MutableLiveData<ChildModel?>()
    var lessonList = ArrayList<ChildModel?>()
    var sectionList = ArrayList<SectionModel?>()
    var playSignVideoLiveData = MutableLiveData<EventObserver<ChildModel?>>()
    var transcriptLiveData = MutableLiveData<String>()
    var isSignVideoPlaying = false
    override fun onApiRetry(apiCode: String) {

        when (apiCode) {
            ApiEndPoints.API_GET_DETAIL_OF_CONTENT -> {
                getSectionContentDetails()
            }
        }
    }


    fun getSectionContentDetails() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = if (modType == MODTYPE.LEARNER) {
                repo.getSectionItemDetails(
                    hashMapOf(
                        "CourseId" to courseId,
                        "LectureId" to lectureId
                    )
                )

            } else {
                repo.getSectionItemDetailsMode(
                    hashMapOf(
                        "CourseId" to courseId,
                        "LectureId" to lectureId
                    )
                )
            }


            withContext(Dispatchers.IO) {

                response.collect {
//                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as BaseResponse<ChildModel>).resource
////                        assessmentDetailLiveData.postValue(resource)
//                        lectureLiveData.postValue(resource)
//                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun postVideoAudioTime(i: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.sendAudioVideoTime(
                hashMapOf(
                    "courseId" to courseId,
                    "sectionId" to sectionId,
                    "lectureId" to lectureId,
                    "duration" to lessonList[i]?.lectureContentDuration.toString()
                )
            )
            withContext(Dispatchers.IO) {
                response.collect {
//                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as BaseResponse<ChildModel>).resource
////                        assessmentDetailLiveData.postValue(resource)
//                        lectureLiveData.postValue(resource)
//                    }
                    updateResponseObserver(it)
                }
            }
        }

    }
}