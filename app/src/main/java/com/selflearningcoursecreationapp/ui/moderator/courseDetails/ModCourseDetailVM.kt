package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.course.CourseComments
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModCourseDetailVM(private val repo: ModCourseDetailRepo) : BaseViewModel() {

    var courseId: Int = 0
    var requestType = MutableLiveData<Int>()
    var status: Int = 0
    var id: Int = 0

    var courseData = MutableLiveData<CourseData>().apply {
    }
    var quizData = MutableLiveData<QuizData?>()
    var assessmentData = MutableLiveData<QuizData?>()

    var commentStatus = MutableLiveData<Boolean?>()
    var updatedLecComments = MutableLiveData<Boolean?>()
    var sectionData = MutableLiveData<ArrayList<SectionModel>>().apply {
        value = ArrayList()
    }

    fun getCourseDetail() {
        val map = HashMap<String, Any>()
        map["courseId"] = courseId
        map["courseModeratorId"] = id
        map["requestType"] = requestType.value ?: 0
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.modCourseDetail(map, status)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseData>).resource
                        resource?.sectionData?.forEach {
                            it.isVisible = false
                        }
                        courseData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }

    }

    fun getAssessment() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getAssessment(courseData.value?.assessmentId ?: 0)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as? BaseResponse<QuizData>)?.resource?.let { data ->
                            quizData.postValue(data)
                            data.list?.forEach { quesData ->
                                quesData.isEnabled = false
                                quesData.ansMarked = true
                                quesData.isExpanded = true
                            }
                            assessmentData.postValue(data)
                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }

    }

    fun getLessonList() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseSections(courseId, status)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<ArrayList<SectionModel>>).resource
                        resource?.forEach { it.isVisible = false }
                        sectionData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun addComment(position: Int, comment: String, type: Int) {
        val map = HashMap<String, Any>()
        if (type == Constant.CLICK_DELETE_COMMENT) {
            map["CommentSectionId"] =
                courseData.value?.sectionData?.get(position)?.commentSectionId ?: 0
        } else if (type == Constant.CLICK_ADD_COMMENT) {
            map["courseId"] = courseData.value?.courseId ?: 0
            map["sectionId"] = courseData.value?.sectionData?.get(position)?.sectionId ?: 0
            map["comment"] = comment
            map["courseModeratorId"] = id
        } else {
            map["sectionId"] = courseData.value?.sectionData?.get(position)?.sectionId ?: 0
            map["comment"] = comment
            map["courseModeratorId"] = id
            map["CommentSectionId"] =
                courseData.value?.sectionData?.get(position)?.commentSectionId ?: 0

            showLog(
                "comment id",
                "is   " + courseData.value?.sectionData?.get(position)?.commentSectionId
            )
        }

        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addComment(map, type)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as BaseResponse<SectionModel>).resource
                        (it.value as BaseResponse<SectionModel>).resource?.let { section ->
                            if (type == Constant.CLICK_DELETE_COMMENT) {
                                courseData.value?.sectionData?.apply {
                                    get(position)?.moderatorComment =
                                        null
                                }
                                commentStatus.postValue(true)


                            } else {

//                                courseData.value?.sectionData?.apply { set(position, section) }


                                courseData.value?.sectionData?.apply {
                                    get(position)?.moderatorComment = section.moderatorComment
                                    get(position)?.commentCreatedDate = section.commentCreatedDate
                                    get(position)?.commentSectionId = section.commentSectionId
                                }
                                commentStatus.postValue(false)
                            }


                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }

    }


    fun addCommentLec(position: Int, comment: String, type: Int, innerposition: Int) {
        val map = HashMap<String, Any>()
        if (type == Constant.CLICK_DELETE_COMMENT_LEC) {
            map["commentLectureId"] =
                courseData.value?.sectionData?.get(position)?.lessonList?.get(innerposition)?.lectureCommentId
                    ?: 0

        } else if (type == Constant.CLICK_ADD_COMMENT_LEC) {
            map["courseId"] = courseData.value?.courseId ?: 0
            map["sectionId"] = courseData.value?.sectionData?.get(position)?.sectionId ?: 0
            map["comment"] = comment
            map["courseModeratorId"] = id
            map["lectureId"] =
                courseData.value?.sectionData?.get(position)?.lessonList?.get(innerposition)?.lectureId
                    ?: 0

        } else {
            map["lectureId"] =
                courseData.value?.sectionData?.get(position)?.lessonList?.get(innerposition)?.lectureId
                    ?: 0
            map["comment"] = comment
            map["courseModeratorId"] = id
            map["commentLectureId"] =
                courseData.value?.sectionData?.get(position)?.lessonList?.get(innerposition)?.lectureCommentId
                    ?: 0

        }

        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addCommentLec(map, type)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as BaseResponse<SectionModel>).resource
                        (it.value as BaseResponse<ChildModel>).resource?.let { lecture ->
                            if (type == Constant.CLICK_DELETE_COMMENT_LEC) {
                                courseData.value?.sectionData?.get(position)?.lessonList?.apply {
                                    get(innerposition)?.lectureComment =
                                        null
                                }
                                commentStatus.postValue(true)


                            } else {
//                                sectionData.value?.removeAt(position)
//                                courseData.value?.sectionData?.get(position)?.lessonList?.set(innerposition,
//                                    lecture
//                                )
//                                updatedLecComments.postValue(courseData.value?.sectionData?.get(position)?.lessonList?.get(innerposition))
                                courseData.value?.sectionData?.get(position)?.lessonList?.apply {
                                    set(
                                        innerposition,
                                        lecture
                                    )
                                }
                                commentStatus.postValue(false)
//                                updateResponseObserver(  Resource.Success(
//                                    lecture,
//                                    ApiEndPoints.API_COMMENT_LECTURE
//                                ))
                            }


                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }

    }


    fun addCommentMisc(position: Int, comment: String, type: Int, commentType: Int) {
        val map = HashMap<String, Any>()
        if (type == Constant.CLICK_DELETE_COMMENT) {
            map["CommentLectureId"] =
                courseData.value?.courseComments?.get(position)?.id ?: 0
        } else if (type == Constant.CLICK_ADD_COMMENT) {
            map["courseId"] = courseData.value?.courseId ?: 0
            map["type"] = commentType ?: 0
            map["comment"] = comment
            map["courseModeratorId"] = id

        } else {
            map["comment"] = comment

            map["CommentId"] = courseData.value?.courseComments?.get(position)?.id ?: 0
        }

        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addCommentMisc(map, type)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseComments>)

                        updatedLecComments.postValue(true)
                        if (type == Constant.CLICK_DELETE_COMMENT) {

//                                courseData.value?.courseComments?.apply { set(position, null) }
                            courseData.value?.courseComments?.removeAt(position)
                            commentStatus.postValue(true)


                        } else if (type == Constant.CLICK_ADD_COMMENT) {
                            resource.resource?.let { it1 ->
                                courseData.value?.courseComments?.add(
                                    it1
                                )
                            }
//                                courseData.value?.sectionData?.apply { set(position, section) }
                            commentStatus.postValue(false)
                        } else {

                            courseData.value?.courseComments?.apply {
                                resource.resource?.let { it1 ->
                                    set(
                                        position,
                                        it1
                                    )
                                }
                            }
//                                courseData.value?.sectionData?.apply { set(position, section) }
                            commentStatus.postValue(false)
                        }

                        updateResponseObserver(
                            Resource.Success(
                                resource,
                                ApiEndPoints.API_COMMENT_MISC
                            )
                        )


                    }
                    updateResponseObserver(it)
                }
            }
        }

    }


    fun updateRequestStatus(status: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Int>()
            map["id"] = id ?: 0
            map["status"] = status ?: 0
            val response = repo.updateCourseRequest(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseData>)
//                        onViewEvent(PagerViewEventsRequest.Remove(data))
                        resource.statusCode = status
                        updateResponseObserver(
                            Resource.Success(
                                resource,
                                ApiEndPoints.API_UPDATE_MOD_REQUEST
                            )
                        )
                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }

    }

    fun updateCourseStatus(status: Int, comment: String? = "") {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["id"] = id ?: 0
            map["status"] = status ?: 0
            map["comment"] = comment ?: ""
            val response = repo.updateCourseStatus(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseData>)
//                        onViewEvent(PagerViewEventsRequest.Remove(data))
                        resource.statusCode = status
                        updateResponseObserver(
                            Resource.Success(
                                resource,
                                ApiEndPoints.API_UPDATE_MOD_COURSE_STATUS
                            )
                        )
                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }

    }


    override fun onApiRetry(apiCode: String) {

    }
}