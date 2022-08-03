package com.selflearningcoursecreationapp.ui.bottom_home


import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.CourseTypeModel
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


class HomeVM(private val repo: HomeRepo) : BaseViewModel() {
    var isTextSearch: Boolean = false
    var currentPage = 1
    private var totalPage = 2
    var recyclerViewState: Parcelable? = null
    var otp = ""


// Restore state

    var searchLiveData = MutableLiveData<String>()
    var selectedFilters = ArrayList<SelectedFilterData?>()
    var selectedCategory: ArrayList<CategoryData> = ArrayList()
    var screenTitle: String = ""
    var courseLiveData = MutableLiveData<ArrayList<CourseTypeModel>>().apply {
        value = arrayListOf()
    }
    var fromOther = false
    var isFirst = true
    var otherCourseLiveData = MutableLiveData<ArrayList<CourseData>>().apply {
        value = arrayListOf()
    }

    var categories = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = arrayListOf()
    }

    var wishlistLiveData = MutableLiveData<EventObserver<UserProfile>>().apply {
        value = EventObserver(UserProfile())
    }


    var adapterPosition: Int = 0
    var childPosition: Int = 0
    var courseId: Int = 0

    init {
        getUserData()
    }


    fun homeCourse() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repo.homeCourse(getFilterPayload())
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    isFirst = false
                    val resource = (it.value as BaseResponse<ArrayList<CourseTypeModel>>).resource
                    courseLiveData.postValue(resource)
                }
                updateResponseObserver(it)
            }
        }
    }

    fun getCourses() {
        if (SelfLearningApplication.token.isNullOrEmpty()) {
            guestHomeCourse()
        } else {
            homeCourse()
        }
    }

    fun guestHomeCourse() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repo.guestHomeCourse(getFilterPayload())
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    isFirst = false
                    val resource = (it.value as BaseResponse<ArrayList<CourseTypeModel>>).resource
                    courseLiveData.postValue(resource)
                }
                updateResponseObserver(it)
            }
        }
    }


    fun homeCategories() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.homeCategories()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val resource = (it.value as BaseResponse<ArrayList<CategoryData>>).resource
                    categories.postValue(resource)
                }
                updateResponseObserver(it)
            }
        }
    }


    fun addWishlist() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            val coursePair = if (fromOther) {
                otherCourseLiveData.value?.get(adapterPosition)?.let {


                    Pair(it.courseId, it.courseWishlisted == 0)
//                    it.courseWishlisted = if (it.courseWishlisted == 0) 1 else 0
                }

            } else {
                courseLiveData.value?.get(adapterPosition)?.let {

                    Pair(
                        it.courses?.get(childPosition)?.courseId,
                        it.courses?.get(childPosition)?.courseWishlisted == 0
                    )
//                    it.courses!![childPosition].courseWishlisted =
//                        if (it.courses!![childPosition].courseWishlisted == 0) 1 else 0
                }
            }


            map["courseId"] = coursePair?.first.toString()
            map["addToWishlist"] = coursePair?.second ?: true
            val response = repo.addWishlist(map, false)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        setWishlist(coursePair)
//                        if (fromOther) {
//                            otherCourseLiveData.value?.get(adapterPosition)?.apply {
//                                  courseWishlisted = if (courseWishlisted == 0) 1 else 0
//                            }
//
//                        } else {
//                            courseLiveData.value?.get(adapterPosition)?.apply {
//
//                                courses!![childPosition].courseWishlisted =
//                                    if (courses!![childPosition].courseWishlisted == 0) 1 else 0
//                            }
//                        }


                        val resource = (it.value as BaseResponse<UserProfile>).resource
                        wishlistLiveData.postValue(EventObserver(resource ?: UserProfile()))
                        updateResponseObserver(it)
                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }


    private fun setWishlist(coursePair: Pair<Int?, Boolean>?) {
        courseLiveData.value?.forEach { courseTypeModel ->
            courseTypeModel.courses?.forEach { data ->
                if (data.courseId == coursePair?.first) {
                    data.courseWishlisted = if (coursePair?.second == true) 1 else 0
                }

            }

        }
        otherCourseLiveData.value?.forEach { data ->
            if (data.courseId == coursePair?.first) {
                data.courseWishlisted = if (coursePair?.second == true) 1 else 0
            }

        }

    }

    fun unmarkWishlist() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId
            map["addToWishlist"] = false
            val response = repo.addWishlist(map, true)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<UserProfile>).resource
                        wishlistLiveData.postValue(EventObserver(resource ?: UserProfile()))
                    }
                    updateResponseObserver(it)
                }
            }
        }

    fun purchaseCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            if (fromOther) {
                otherCourseLiveData.value?.get(adapterPosition)?.let {
                    if (!otp.isNullOrEmpty()) {
                        map["unlockCode"] = otp
                    }
                    map["courseId"] = it.courseId ?: 0
                    map["courseType"] = it.courseTypeId ?: 0

                }
            } else {
                courseLiveData.value?.get(adapterPosition)?.let {
                    if (!otp.isNullOrEmpty()) {
                        map["unlockCode"] = otp
                    }
                    map["courseId"] = it.courses?.get(childPosition)?.courseId ?: 0
                    map["courseType"] = it.courses?.get(childPosition)?.courseTypeId ?: 0

                }
            }

            val response = repo.purchaseCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        if (fromOther) {
                            val data = otherCourseLiveData.value?.apply {

                                get(adapterPosition)?.let { data ->
                                    data.userCourseStatus = 1
                                }
                            }
                            otherCourseLiveData.postValue(data)
                        } else {
                            val data = courseLiveData.value?.apply {
                                get(adapterPosition)?.let { model ->
                                    model.courses?.get(childPosition)?.userCourseStatus = 1
                                }
                            }

                            courseLiveData.postValue(data)
                        }
                    }
                    updateResponseObserver(it)
                }
            }

        }

    fun buyRazorPayCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            if (fromOther) {
                otherCourseLiveData.value?.get(adapterPosition)?.let {
                    map["courseId"] = it.courseId ?: 0
                    map["amount"] = it.courseFee ?: ""
                    map["currency"] = "INR"
                }
            } else {
                courseLiveData.value?.get(adapterPosition)?.let {
                    map["courseId"] = it.courses?.get(childPosition)?.courseId ?: 0
                    map["amount"] = it.courses?.get(childPosition)?.courseFee ?: ""
                    map["currency"] = "INR"
                }
            }

            val response = repo.buyRazorPayCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }

//    fun reset() {
//        currentPage = 1
//        totalPage = 2
//        courseLiveData.value?.clear()
//    }

    fun getOtherCourses() {
        if (currentPage < totalPage) {


            val filterPayload = getFilterPayload(true)
            viewModelScope.launch(coroutineExceptionHandle) {
                val response =
                    if (SelfLearningApplication.token.isNullOrEmpty()) repo.getGuestOtherCourses(
                        filterPayload
                    ) else repo.getOtherCourses(
                        filterPayload
                    )
                withContext(Dispatchers.IO) {
                    response.collect {
                        if (it is Resource.Success<*>) {
                            isFirst = false
                            (it.value as BaseResponse<AllCoursesResponse>).resource?.let { resource ->
                                totalPage = resource.resultCount ?: 2
                                val list = otherCourseLiveData.value ?: ArrayList()
                                if (resource.currentPage == 1) {
                                    list?.clear()
                                    currentPage = 1
                                }
                                currentPage += 1
                                list?.addAll(resource.coursesList ?: ArrayList())
                                otherCourseLiveData.postValue(list)
                            }

                        }
                        updateResponseObserver(it)

                    }
                }
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_HOME_COURSES -> {
                homeCourse()
            }

            ApiEndPoints.API_HOME_GUESTCOURSES -> {
                guestHomeCourse()
            }
            ApiEndPoints.API_HOME_TAB_CATE -> {
                homeCategories()
            }
            ApiEndPoints.API_HOME_WISHLIST + "/false" -> {
                addWishlist()
            }
            ApiEndPoints.API_HOME_WISHLIST + "/true" -> {
                unmarkWishlist()
            }
            ApiEndPoints.API_PURCHASE_COURSE -> {

                purchaseCourse()
            }
        }

    }

    private fun getFilterPayload(fromAll: Boolean = false): JSONObject {
        return try {


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
            if (fromAll) {
                map.put("pageNumber", currentPage)
                map.put("courseType", CourseScreenType.ALL_COURSES)
            }
            map
        } catch (e: Exception) {
            e.printStackTrace()
            JSONObject()
        }

    }

    fun reset() {
        currentPage = 1
        totalPage = 2
        isFirst = true
        courseLiveData.value?.clear()
        otherCourseLiveData.value?.clear()
    }
}