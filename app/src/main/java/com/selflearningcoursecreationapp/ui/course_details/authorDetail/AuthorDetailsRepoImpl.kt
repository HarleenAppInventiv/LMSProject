package com.selflearningcoursecreationapp.ui.course_details.authorDetail

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class AuthorDetailsRepoImpl(private var apiService: ApiService) : AuthorDetailsFragmentRepo {

    override suspend fun getAuthorDetails(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AuthorDetailsData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AuthorDetailsData>> {
                return apiService.getAuthorDEtails(map)
            }
        }.safeApiCall(ApiEndPoints.API_COURSE_AUTHOR_DETAIL).flowOn(Dispatchers.IO)
    }

    override suspend fun getCoAuthorDetails(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AuthorDetailsData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AuthorDetailsData>> {
                return apiService.getCoAuthorDEtails(map)
            }
        }.safeApiCall(ApiEndPoints.API_COURSE_COAUTHOR_DETAIL).flowOn(Dispatchers.IO)
    }

    override suspend fun addWishlist(
        map: HashMap<String, Any>,
        fromWishlist: Boolean
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.addWishlist(map)
            }
        }.safeApiCall(ApiEndPoints.API_WISHLIST + "/$fromWishlist").flowOn(Dispatchers.IO)
    }

    override suspend fun purchaseCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.purchaseCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_PURCHASE_COURSE).flowOn(Dispatchers.IO)
    }

    override suspend fun buyRazorPayCourse(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return apiService.buyRazorPayCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_RAZORPAY_COURSE).flowOn(Dispatchers.IO)
    }


}
