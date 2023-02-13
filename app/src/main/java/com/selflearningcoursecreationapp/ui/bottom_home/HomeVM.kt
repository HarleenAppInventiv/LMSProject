package com.selflearningcoursecreationapp.ui.bottom_home


import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.BR
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.ApiService
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


class HomeVM(private val repo: HomeRepo, private var apiService: ApiService) : BaseViewModel() {
    var isTextSearch: Boolean = false
    var currentPage = 1
    private var totalPage = 2
    var recyclerViewState: Parcelable? = null
    var otp = ""

    init {
        getUserData()
    }

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

    var wishlistLiveData = MutableLiveData<EventObserver<Pair<Int?, Boolean>>>().apply {

    }


    var adapterPosition: Int = 0
    var childPosition: Int = 0
    var courseId: Int = 0

    init {
        getUserData()
    }


    fun patchNotification() = viewModelScope.launch(coroutineExceptionHandle) {

//        val response = repo.patchNotification(
//            notificationLiveData.value?.get(adapterPosition)?.notificationId ?: 0
//        )
//        withContext(Dispatchers.IO) {
//            response.collect {
////                if (it is Resource.Success<*>) {
////                    notificationLiveData.value?.get(adapterPosition)?.isRead= true
////                }
//                updateResponseObserver(it)
//            }
//        }
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

    fun getNotificationCount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.getNotificationCount()
        withContext(Dispatchers.IO) {
            response.collect {
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
//        viewModelScope.async {
        val response = repo.guestHomeCourse(getFilterPayload())
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    isFirst = false
                    val resource =
                        (it.value as BaseResponse<ArrayList<CourseTypeModel>>).resource
                    courseLiveData.postValue(resource)
                }
                updateResponseObserver(it)
            }
//            }
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
                }

            } else {
                courseLiveData.value?.get(adapterPosition)?.let {

                    Pair(
                        it.courses?.get(childPosition)?.courseId,
                        it.courses?.get(childPosition)?.courseWishlisted == 0
                    )
                }
            }


            map["courseId"] = coursePair?.first.toString()
            map["addToWishlist"] = coursePair?.second ?: true
            val response = repo.addWishlist(map, false)
            withContext(Dispatchers.IO) {
                response.collect {

                    courseLiveData.value?.forEach { typeModel ->
                        typeModel.courses?.forEach { course ->
                            if (course.courseId == coursePair?.first) {
                                course.bookmarkProgress = it is Resource.Loading
                                course.notifyPropertyChanged(BR.bookmarkProgress)
                                course.notifyChange()
                            }
                        }

                    }
                    otherCourseLiveData.value?.forEach { data ->
                        if (data.courseId == coursePair?.first) {
                            data.bookmarkProgress = it is Resource.Loading
                            data.notifyPropertyChanged(BR.bookmarkProgress)
                            data.notifyChange()
                        }

                    }


                    if (it is Resource.Success<*>) {
//                        setWishlist(coursePair)

                        val resource = (it.value as BaseResponse<UserProfile>).resource
                        updateResponseObserver(
                            Resource.Success(
                                coursePair,
                                ApiEndPoints.API_HOME_WISHLIST
                            )
                        )
                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }


    fun setWishlist(coursePair: Pair<Int?, Boolean>?) {
        courseLiveData.value?.let {
            it.forEach { courseTypeModel ->
                courseTypeModel.courses?.forEach { data ->
                    data.apply {
                        if (data.courseId == coursePair?.first) {
                            data.courseWishlisted = if (coursePair?.second == true) 1 else 0
                            data.notifyPropertyChanged(BR.courseWishlisted)
                            data.notifyChange()
                        }
                    }


                }

            }
        }
        otherCourseLiveData.value?.let {
            it.forEach { data ->
                if (data.courseId == coursePair?.first) {
                    data.courseWishlisted = if (coursePair?.second == true) 1 else 0
                    data.notifyPropertyChanged(BR.courseWishlisted)
                    data.notifyChange()
                }

            }
        }


//         courseLiveData.postValue(courseDataList)
//         otherCourseLiveData.postValue(otherCourseLiveData.value)
//        wishlistLiveData.postValue(EventObserver(coursePair ?: Pair(0, false)))

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
                        wishlistLiveData.postValue(EventObserver(Pair(courseId, false)))
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

                                get(adapterPosition).let { data ->
                                    data.userCourseStatus = 1
                                }
                            }
                            otherCourseLiveData.postValue(data)
                        } else {
                            val data = courseLiveData.value?.apply {
                                get(adapterPosition).let { model ->
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

    var stateId = ""
    fun buyRazorPayCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            if (fromOther) {
                otherCourseLiveData.value?.get(adapterPosition)?.let {
                    map["courseId"] = it.courseId ?: 0
                    map["amount"] = it.courseFee ?: ""
                    map["currency"] = "INR"
                    map["stateId"] = stateId
                }
            } else {
                courseLiveData.value?.get(adapterPosition)?.let {
                    map["courseId"] = it.courses?.get(childPosition)?.courseId ?: 0
                    map["amount"] = it.courses?.get(childPosition)?.courseFee ?: ""
                    map["currency"] = "INR"
                    map["stateId"] = stateId
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
                                val totalPage =
                                    (resource?.totalCount ?: 0) / (resource?.pageSize ?: 8)
                                val remCount =
                                    (resource?.totalCount ?: 0) % (resource?.pageSize ?: 8)
                                this@HomeVM.totalPage =
                                    totalPage + (if (remCount > 0) 1 else 0)
//                                totalPage = resource.resultCount ?: 2
                                val list = otherCourseLiveData.value ?: ArrayList()
                                if (resource.currentPage == 1) {
                                    list.clear()
                                    currentPage = 1
                                }
                                currentPage += 1
                                list.addAll(resource.coursesList ?: ArrayList())
                                otherCourseLiveData.postValue(list)
                            }
                            delay(500)
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
                callCombinedApis()
            }

            ApiEndPoints.API_HOME_GUESTCOURSES -> {
                callCombinedApis()
            }
            ApiEndPoints.API_HOME_TAB_CATE -> {
                callCombinedApis()
            }
            ApiEndPoints.API_ALL_COURSES -> {
                callCombinedApis()
            }
            ApiEndPoints.API_GUEST_ALL_COURSES -> {
                callCombinedApis()
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
                catObj.put("operatorType", if (valArray.length() > 1) 3 else 1)
                filterArray.put(
                    catObj
                )
            }


            if (filterArray.length() != 0) {
                map.put("searchFields", filterArray)
            }

            if (fromAll) {
                map.put("pageNumber", currentPage)
//                if (SelfLearningApplication.token.isNullOrEmpty()){
//
//                }
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


    fun callCombinedApis() {
        viewModelScope.launch(coroutineExceptionHandle) {
            updateResponseObserver(Resource.Loading(apiCode = ApiEndPoints.HOME_SUCCESS))
            val otherCourses =
                if (SelfLearningApplication.token.isNullOrEmpty())
                    repo.getGuestOtherCourses(
                        getFilterPayload(true)
                    ) else repo.getOtherCourses(
                    getFilterPayload(true)
                )

            val mainCourses = if (SelfLearningApplication.token.isNullOrEmpty()) {
                repo.guestHomeCourse(getFilterPayload())
            } else {
                repo.homeCourse(getFilterPayload())
            }


            val category = repo.homeCategories()

            combine(category, otherCourses, mainCourses) { t1, t2, t3 ->
                val list = ArrayList<Resource>()
                list.add(t1)// category
                list.add(t2)// other courses
                list.add(t3)// main courses
                list
            }.collect { list ->

                var successCount = 0
                var failureCount = 0
                list.forEachIndexed { index, resource ->
                    if (resource is Resource.Success<*>) {
                        successCount += 1
                        when (index) {
                            0 -> {
                                handleCategoryResponse(resource)
                            }
                            1 -> {
                                handleOtherCoursesResponse(resource)
                            }
                            else -> {
                                handleMainCoursesResponse(resource)
                            }
                        }
                    } else if (resource is Resource.Failure) {
                        failureCount += 1
                        updateResponseObserver(resource)
                        return@collect
                    } else if (resource is Resource.Retry) {
                        updateResponseObserver(resource)
                    }
                }

                if (successCount + failureCount == 3) {
                    updateResponseObserver(Resource.Success(true, ApiEndPoints.HOME_SUCCESS))
                }
            }

        }
    }

    private fun handleMainCoursesResponse(response: Resource.Success<*>) {
        isFirst = false
        val resource = (response.value as BaseResponse<ArrayList<CourseTypeModel>>).resource

        resource?.removeAll { it.courses.isNullOrEmpty() }
        courseLiveData.postValue(resource)

    }

    private fun handleOtherCoursesResponse(response: Resource.Success<*>) {
        isFirst = false
        (response.value as BaseResponse<AllCoursesResponse>).resource?.let { resource ->
            totalPage = resource.resultCount ?: 2
            val list = otherCourseLiveData.value ?: ArrayList()
            if (resource.currentPage == 1) {
                list.clear()
                currentPage = 1
            }
            currentPage += 1
            list.addAll(resource.coursesList ?: ArrayList())
            otherCourseLiveData.postValue(list)
        }

    }

    private fun handleCategoryResponse(response: Resource.Success<*>) {
        val resource = (response.value as BaseResponse<ArrayList<CategoryData>>).resource
        categories.postValue(resource)
    }


    fun updateCourse(courseId: Int?) {
        courseLiveData.value?.let {
            it.forEach { courseTypeModel ->
                courseTypeModel.courses?.forEach { data ->
                    data.apply {
                        if (data.courseId == courseId) {
                            data.userCourseStatus = 1
                            data.notifyPropertyChanged(BR.userCourseStatus)
                            data.notifyChange()
                        }
                    }


                }

            }
        }
        otherCourseLiveData.value?.let {
            it.forEach { data ->
                if (data.courseId == courseId) {
                    data.userCourseStatus = 1
                }

            }
        }


    }

    fun updateCourseRating(courseId: Int?, rating: String?, reviewCount: Long?) {
        courseLiveData.value?.let {
            it.forEach { courseTypeModel ->
                courseTypeModel.courses?.forEach { data ->
                    data.apply {
                        if (data.courseId == courseId) {
                            data.averageRating = rating
                            data.totalReviews = reviewCount
//                            data.notifyPropertyChanged(BR.userCourseStatus)
                            data.notifyChange()
                        }
                    }


                }

            }
        }
        otherCourseLiveData.value?.let {
            it.forEach { data ->
                if (data.courseId == courseId) {
                    data.averageRating = rating
                    data.totalReviews = reviewCount
                }

            }
        }


    }
}