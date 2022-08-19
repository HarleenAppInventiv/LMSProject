package com.selflearningcoursecreationapp.ui.course_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.ContentAssessmentData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CourseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailVM(private var repo: CourseDetailRepo) : BaseViewModel() {
    var addReviewRequest = AddReviewRequestModel()
    var selectedFilters = ArrayList<SelectedFilterData?>()
    var otp = ""
    var courseContentType: Boolean = false
    var courseId: Int = 0
    var courseData = MutableLiveData<CourseData?>()
    var sectionData = MutableLiveData<ArrayList<SectionModel>>().apply {
        value = ArrayList()
    }

    var userCourseStatus = MutableLiveData<Int>()

    var purchaseCourseLiveData = MutableLiveData<OrderData>().apply {
        value = OrderData()
    }

    var certificateLiveData = MutableLiveData<UserProfile>().apply {
        value = UserProfile()
    }

    var assessmentDetailLiveData = MutableLiveData<ContentAssessmentData?>()

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

    fun getCourseDetail() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseDetail(courseId)
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

    fun getLessonList() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseSections(courseId)
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
            courseData.value?.let {
                if (!otp.isNullOrEmpty()) {
                    map["unlockCode"] = otp
                }
                map["courseId"] = it.courseId ?: 0
                map["courseType"] = it.courseTypeId ?: 0
                val response = repo.purchaseCourse(map)
                withContext(Dispatchers.IO) {
                    response.collect {
                        if (it is Resource.Success<*>) {
                            val resource = (it.value as BaseResponse<OrderData>).resource
                            courseData?.value?.userCourseStatus = CourseStatus.ENROLLED
                            purchaseCourseLiveData.postValue(resource)
                        }
                        updateResponseObserver(it)
                    }
                }
            }
        }

    fun buyRazorPayCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            courseData?.value?.let {
                map["courseId"] = it.courseId ?: 0
                map["amount"] = it.courseFee ?: ""
                map["currency"] = "INR"
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
                            certificateLiveData.postValue(resource)
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


    fun assessmentDetails() =
        viewModelScope.launch(coroutineExceptionHandle) {
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
}