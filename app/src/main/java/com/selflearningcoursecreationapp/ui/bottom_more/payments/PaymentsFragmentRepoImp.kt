package com.selflearningcoursecreationapp.ui.bottom_more.payments

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.MyPaymentEarningDataModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.MyPurchaseDataModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.PurchaseDetailDataModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.RemainingWalletBalanceData
import com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request.MinAmountRequestModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet.WalletDataModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class PaymentsFragmentRepoImp(private val apiService: ApiService) : PaymentsFragmentRepo {


    override suspend fun remainingWalletBalance(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<RemainingWalletBalanceData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<RemainingWalletBalanceData>> {
                return apiService.remainingWalletBalance()
            }
        }.safeApiCall(ApiEndPoints.API_PAYMENT_REMAINING_WALLET_BALANCE).flowOn(Dispatchers.IO)
    }

    override suspend fun userPurchases(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<MyPurchaseDataModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<MyPurchaseDataModel>> {
                return apiService.getUserPurchases(map)
            }
        }.safeApiCall(ApiEndPoints.API_PAYMENT_USER_PURCHASES).flowOn(Dispatchers.IO)
    }

    override suspend fun userEarnings(map: Int, i: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<MyPaymentEarningDataModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<MyPaymentEarningDataModel>> {
                return apiService.getUserEarnings(map, i)
            }
        }.safeApiCall(ApiEndPoints.API_PAYMENT_USER_EARNINGS).flowOn(Dispatchers.IO)
    }

    override suspend fun paymentWithdrawRequest(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.getPaymentWithdrawRequest(map)
            }
        }.safeApiCall(ApiEndPoints.API_WITHDRAW_REQUEST).flowOn(Dispatchers.IO)
    }

    override suspend fun minWithdrawAmount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<MinAmountRequestModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<MinAmountRequestModel>> {
                return apiService.getMinWithdrawAmount()
            }
        }.safeApiCall(ApiEndPoints.API_MIN_WITHDRAW_AMOUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun getPurchasedCourseDetail(courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<PurchaseDetailDataModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<PurchaseDetailDataModel>> {
                return apiService.getPurchasedCourseDetail(courseId)
            }
        }.safeApiCall(ApiEndPoints.API_PAYMENT_COURSE_DETAILS).flowOn(Dispatchers.IO)
    }

    override suspend fun getWalletHistory(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<WalletDataModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<WalletDataModel>> {
                return apiService.walletHistory(map)
            }
        }.safeApiCall(ApiEndPoints.API_WALLET_HISTORY).flowOn(Dispatchers.IO)
    }


}