package com.selflearningcoursecreationapp.ui.profile.bookmark

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.course_details.ratings.ReviewPagingDataSource
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

interface WishListRepository {
    suspend fun getWishListData(): LiveData<PagingData<CourseData>>
    suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource>

    suspend fun getReviewListData(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>>
}

class WishListRepositoryImpl(private val service: ApiService) : WishListRepository {
    override suspend fun getWishListData(): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingDataSource(service)
            }
        ).liveData
    }

    override suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return service.purchaseCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_PURCHASE_COURSE).flowOn(Dispatchers.IO)
    }


    override suspend fun getReviewListData(data: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewPagingDataSource(service, data)
            }
        ).liveData
    }

    override suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<OrderData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<OrderData>> {
                return service.buyRazorPayCourse(map)
            }
        }.safeApiCall(ApiEndPoints.API_RAZORPAY_COURSE).flowOn(Dispatchers.IO)
    }

}