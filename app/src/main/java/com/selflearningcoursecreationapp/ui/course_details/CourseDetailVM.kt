package com.selflearningcoursecreationapp.ui.course_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.ContentAssessmentData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import com.selflearningcoursecreationapp.ui.course_details.model.ShareLinkUrl
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailVM(private var repo: CourseDetailRepo) : BaseViewModel() {
    var addReviewRequest = AddReviewRequestModel()
    var selectedFilters = ArrayList<SelectedFilterData?>()
    var otp = ""
    var shareUrl = ""
    var courseContentType = ""
    var moderatorRequestId = ""
    var sectionId: Int = 0
    var courseId: Int = 0
    var courseTypeId: Int? = 0
    var courseType: Int = 0
    var modType: Int = 0
    var lectureId: Int = 0
    var share: Int = 0
    var courseData = MutableLiveData<CourseData?>()
    var lessonList = ArrayList<ChildModel?>()

    var sectionData = MutableLiveData<ArrayList<SectionModel>>().apply {
        value = ArrayList()
    }

    var sectionList = ArrayList<SectionModel?>()
    var viewPagerScroll = MutableLiveData<Boolean>().apply {
        value = true
    }
    var userCourseStatus = MutableLiveData<Int>()

    var isInEnd = MutableLiveData<EventObserver<Boolean>>()

    var purchaseCourseLiveData = MutableLiveData<OrderData>().apply {
        value = OrderData()
    }

    var certificateLiveData = MutableLiveData<EventObserver<UserProfile?>>()

    var shareLinkUrlLiveData = MutableLiveData<EventObserver<ShareLinkUrl?>>().apply {

    }

    var assessmentDetailLiveData = MutableLiveData<ContentAssessmentData?>()

    var quizData = MutableLiveData<QuizData>().apply {
        value = QuizData()
    }

    var lectureLiveData = MutableLiveData<ChildModel?>()


    var assessmentData = MutableLiveData<QuizData?>()

    override fun onApiRetry(apiCode: String) {

        when (apiCode) {
            ApiEndPoints.API_COURSE_DETAILS -> {
                getCourseDetail()
            }
            ApiEndPoints.API_COURSE_SECTIONS -> {
                getLessonList()
            }
            ApiEndPoints.API_ADD_REVIEW -> {
                addReviewsRequest()

            }
            ApiEndPoints.API_PURCHASE_COURSE -> {
                purchaseCourse()
            }
        }
    }

    fun getCourseShareLinkUrl() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getShareLink(courseId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<ShareLinkUrl>).resource
                        shareLinkUrlLiveData.postValue(EventObserver(resource))
                        shareUrl = resource?.url.toString()
                    }
                    updateResponseObserver(it)
                }
            }
        }

    }

    fun getCourseDetail(type: String = "") {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseDetail(courseId, type, moderatorRequestId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<CourseData>).resource
                        courseData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }

    }

    fun editToDraft(courseId: Int) = viewModelScope.launch(coroutineExceptionHandle)
    {

        val response = repo.editCourseToDraft(courseId)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }

    }

    fun getLessonList() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseSections(courseId, courseContentType, moderatorRequestId)
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

    fun purchaseCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            courseData.value?.let { cou ->
                if (!otp.isNullOrEmpty()) {
                    map["unlockCode"] = otp
                }
                map["courseId"] = cou.courseId ?: 0
                map["courseType"] = cou.courseTypeId ?: 0
                val response = repo.purchaseCourse(map)
                withContext(Dispatchers.IO) {
                    response.collect {
                        if (it is Resource.Success<*>) {
                            val resource = (it.value as BaseResponse<OrderData>).resource
                            courseData.value?.userCourseStatus = CourseStatus.ENROLLED
                            purchaseCourseLiveData.postValue(resource)
                        }
                        updateResponseObserver(it)
                    }
                }
            }
        }

    var stateId = ""


    fun buyRazorPayCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            courseData.value?.let {
                map["courseId"] = it.courseId ?: 0
                map["amount"] = it.courseFee ?: ""
                map["currency"] = "INR"
                map["stateId"] = stateId
            }


            val response = repo.buyRazorPayCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }


    fun downloadCertificate() =
        viewModelScope.launch(coroutineExceptionHandle) {
            courseData.value?.let { it ->
                val response = repo.downloadCertificate(it.courseId.toString())
                withContext(Dispatchers.IO) {
                    response.collect {
                        if (it is Resource.Success<*>) {
                            val resource = (it.value as BaseResponse<UserProfile>).resource
                            certificateLiveData.postValue(EventObserver(resource))
                        }
                        updateResponseObserver(it)
                    }
                }
            }
        }

    fun downloadAppCertificate() =
        viewModelScope.launch(coroutineExceptionHandle) {
            courseData.value?.let { it ->
                val response = repo.downloadAppreciationCertificate(it.courseId.toString())
                withContext(Dispatchers.IO) {
                    response.collect {
                        if (it is Resource.Success<*>) {
                            val resource = (it.value as BaseResponse<UserProfile>).resource
                            certificateLiveData.postValue(EventObserver(resource))
                        }
                        updateResponseObserver(it)
                    }
                }
            }
        }


    fun addReviewsRequest() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addReviewsRequest(addReviewRequest)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }


    fun assessmentDetails() = viewModelScope.launch(coroutineExceptionHandle) {
        courseData.value?.let {
            val response = repo.contentAssessment(it.courseId.toString())
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource =
                            (it.value as BaseResponse<ContentAssessmentData>).resource
                        assessmentDetailLiveData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun getQuizQuestions(quizId: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getQuizQues(quizId ?: 0)

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as? BaseResponse<QuizData>)?.resource?.let { data ->
                            data.courseId = courseId
                            data.list?.forEach { ques ->
                                ques.isEnabled = false
                                val optionId =
                                    ques.optionList.map { option -> option.id }.joinToString { "," }
                                ques.optionIds = optionId
                                ques.ansMarked =
                                    if (QUIZ.MATCH_COLUMN == ques.questionType) {
                                        ques.optionList.find { option -> option.ansId.isNullOrZero() } == null
                                    } else {
                                        !ques.optionList.filter { option -> option.isSelected == true }
                                            .isNullOrEmpty()
                                    }
                                ques.isExpanded = false
                            }
                            quizData.postValue(data)
                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun getAssessment(assessmentId: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getAssessment(assessmentId)
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
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<ChildModel>).resource
//                        assessmentDetailLiveData.postValue(resource)
                        lectureLiveData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun getStateList() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.stateList()
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }

    var authorUserId = 0

    fun getAuthorDetails() = viewModelScope.launch(coroutineExceptionHandle) {
        var map = HashMap<String, Any>()
        map["pageSize"] = 5
        map["pageNumber"] = 1
        map["userId"] = authorUserId

        val response = repo.getAuthorDetails(map)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }


}