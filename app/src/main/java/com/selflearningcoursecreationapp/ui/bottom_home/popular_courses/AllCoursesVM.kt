package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.course.AllCoursesResponse
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CourseScreenType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class AllCoursesVM(private var repo: AllCoursesRepo) : BaseViewModel() {
    var isTextSearch: Boolean = false
    var selectedFilters = ArrayList<SelectedFilterData?>()
    var selectedCategory: ArrayList<CategoryData> = ArrayList()
    var currentPage = 1
    var searchLiveData = MutableLiveData<String>()
    var courseType: Int? = CourseScreenType.ALL_COURSES
    var adapterPosition = 0
    var isFirst = true
    var otp = ""
    private var totalPage = 2
    var courseLiveData = MutableLiveData<ArrayList<CourseData>>().apply {
        value = ArrayList()
    }
    var wishlistLiveData = MutableLiveData<UserProfile>().apply {
        value = UserProfile()
    }


    fun reset() {
        currentPage = 1
        totalPage = 2
        isFirst = true
        courseLiveData.value?.clear()
    }

    fun getCourses() {
        if (currentPage < totalPage) {
            try {


                val map = JSONObject()
                if (!searchLiveData.value.isNullOrEmpty()) {
                    map.put("generalSearchField", searchLiveData.value ?: "")
                }
                val filterArray = JSONArray()
                selectedFilters.forEach {
                    val valArray = JSONArray()
                    valArray.put(it?.filterOptionValue)
                    val catObj = JSONObject()
                    catObj.put("fieldName", it?.filterName)
                    catObj.put("fieldValue", valArray)
                    catObj.put("operatorType", it?.filterOptionOperatorId)
                    filterArray.put(catObj)
//                filterArray.put(JSONObject(Gson().toJson(it)))

                }

                if (!selectedCategory.isNullOrEmpty()) {
                    val valArray = JSONArray()
                    selectedCategory.forEach {
                        valArray.put("${it.id}")
                    }
                    val catObj = JSONObject()
                    catObj.put("fieldName", "CategorytypeId")
                    catObj.put("fieldValue", valArray)
                    catObj.put("operatorType", 3)
                    filterArray.put(
                        catObj
                    )
                }

                if (filterArray.length() != 0) {
                    map.put("searchFields", filterArray)
                }
                map.put("pageNumber", currentPage)
                map.put("courseType", courseType ?: CourseScreenType.ALL_COURSES)
                viewModelScope.launch(coroutineExceptionHandle) {
                    val response =
                        if (SelfLearningApplication.token.isNullOrEmpty()) repo.getGuestCourses(map) else repo.getCourses(
                            map
                        )
                    withContext(Dispatchers.IO) {
                        response.collect {
                            if (it is Resource.Success<*>) {
                                isFirst = false
                                (it.value as BaseResponse<AllCoursesResponse>).resource?.let { resource ->
                                    totalPage = resource.resultCount ?: 2
                                    if (currentPage == 1) {
                                        courseLiveData.postValue(ArrayList())
                                    }
                                    currentPage += 1
                                    val list = courseLiveData.value
                                    list?.addAll(resource.coursesList ?: ArrayList())
                                    courseLiveData.postValue(list)
                                }

                            }

                            updateResponseObserver(it)
                        }
                    }
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }


    fun addWishlist() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            courseLiveData.value?.get(adapterPosition)?.let {
                map["courseId"] = it.courseId.toString()
                map["addToWishlist"] = it.courseWishlisted == 0

                it.courseWishlisted =
                    if (it.courseWishlisted == 0) 1 else 0
            }
            val response = repo.addWishlist(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<UserProfile>).resource
                        wishlistLiveData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }


    fun purchaseCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            courseLiveData.value?.get(adapterPosition)?.let {
                if (!otp.isNullOrEmpty()) {
                    map["unlockCode"] = otp
                }
                map["courseId"] = it.courseId ?: 0
                map["courseType"] = it.courseTypeId ?: 0
                val response = repo.purchaseCourse(map)
                withContext(Dispatchers.IO) {
                    response.collect {


                        if (it is Resource.Success<*>) {
                            val data = courseLiveData.value?.apply {

                                get(adapterPosition)?.let { data ->
                                    data.userCourseStatus = 1
                                }
                            }
                            courseLiveData.postValue(data)

                        }
                        updateResponseObserver(it)
                    }
                }
            }
        }

    fun buyRazorPayCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()


            courseLiveData.value?.get(adapterPosition)?.let {
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

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_HOME_WISHLIST -> {
                addWishlist()
            }
            ApiEndPoints.API_PURCHASE_COURSE -> {
                purchaseCourse()
            }
            else -> {
                getCourses()
            }
        }
    }
}