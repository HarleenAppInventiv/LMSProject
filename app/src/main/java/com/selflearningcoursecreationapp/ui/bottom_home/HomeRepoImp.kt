package com.selflearningcoursecreationapp.ui.bottom_home

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.CourseTypeModel
import com.selflearningcoursecreationapp.models.course.AllCoursesResponse
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.Response

class HomeRepoImp(private val apiService: ApiService) : HomeRepo {
    override suspend fun homeCourse(map: JSONObject): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<CourseTypeModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<CourseTypeModel>>> {
                return apiService.homeCourses(map.getRequestBody())
            }

        }.safeApiCall(ApiEndPoints.API_HOME_COURSES).flowOn(Dispatchers.IO)
    }


    override suspend fun guestHomeCourse(map: JSONObject): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<CourseTypeModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<CourseTypeModel>>> {
                return apiService.guestHomeCourses(map.getRequestBody())
            }
        }.safeApiCall(ApiEndPoints.API_HOME_GUESTCOURSES).flowOn(Dispatchers.IO)
    }

    override suspend fun homeCategories(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<CategoryData>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<CategoryData>>> {
                return apiService.homeTabCate()
            }
        }.safeApiCall(ApiEndPoints.API_HOME_TAB_CATE).flowOn(Dispatchers.IO)
    }

    override suspend fun addWishlist(map: Map<String, Any>, fromWishlist: Boolean): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.addWishlist(map)
            }
        }.safeApiCall(ApiEndPoints.API_WISHLIST + "/$fromWishlist").flowOn(Dispatchers.IO)
    }

    override suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.purchaseCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_PURCHASE_COURSE).flowOn(Dispatchers.IO)
    }

    override suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.buyRazorPayCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_RAZORPAY_COURSE).flowOn(Dispatchers.IO)
    }

    override suspend fun getOtherCourses(
        map: JSONObject,
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AllCoursesResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AllCoursesResponse>> {
                return apiService.getAllCourses(map.getRequestBody())
            }
        }.safeApiCall(ApiEndPoints.API_ALL_COURSES).flowOn(Dispatchers.IO)

    }

    override suspend fun getGuestOtherCourses(
        map: JSONObject,
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AllCoursesResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AllCoursesResponse>> {
                return apiService.getAllGuestCourses(
                    map.getRequestBody()
                )
            }
        }.safeApiCall(ApiEndPoints.API_GUEST_ALL_COURSES).flowOn(Dispatchers.IO)

    }


}