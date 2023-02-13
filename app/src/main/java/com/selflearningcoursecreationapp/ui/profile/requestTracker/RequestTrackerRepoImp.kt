package com.selflearningcoursecreationapp.ui.profile.requestTracker

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
import com.selflearningcoursecreationapp.models.user.ModeratorStatusResponses
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.ExistsCoAuthorResponse
import com.selflearningcoursecreationapp.ui.moderator.ModeratorDataSource
import com.selflearningcoursecreationapp.ui.moderator.model.RequestCountModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls.PaymentWithdrawData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class RequestTrackerRepoImp(private val apiService: ApiService) : RequestTrackerRepo {
    override suspend fun modStatus(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModeratorStatusResponses>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModeratorStatusResponses>> {
                return apiService.modStatus()
            }
        }.safeApiCall(ApiEndPoints.API_MOD_STATUS).flowOn(Dispatchers.IO)
    }

    override suspend fun requestCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<RequestCountModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<RequestCountModel>> {
                return apiService.requestCount()
            }
        }.safeApiCall(ApiEndPoints.API_REQUEST_COUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun cancelReq(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.cancelReq(map)
            }
        }.safeApiCall(ApiEndPoints.API_CANCEL_REQ).flowOn(Dispatchers.IO)
    }

    override suspend fun getRequestList(hashmap: GetReviewsRequestModel): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ModeratorDataSource(apiService, hashmap)
            }
        ).liveData
    }

    override suspend fun manageInvitation(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.manageCoAuthorInvitation(map)
            }

        }.safeApiCall(ApiEndPoints.API_COAUTHOR_INVITATION).flowOn(Dispatchers.IO)
    }

    override suspend fun paymentWithdrawList(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<PaymentWithdrawData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<PaymentWithdrawData>> {
                return apiService.paymentWithdrawList(map)
            }

        }.safeApiCall(ApiEndPoints.API_PAYMENT_WITHDRAW_LIST).flowOn(Dispatchers.IO)
    }

    override suspend fun existsCoAuthor(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ExistsCoAuthorResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ExistsCoAuthorResponse>> {
                return apiService.existsCoAuthorDetails(courseId)
            }

        }.safeApiCall(ApiEndPoints.API_EXISTS_COAUTHOR).flowOn(Dispatchers.IO)
    }

}