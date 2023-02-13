package com.selflearningcoursecreationapp.ui.my_bank

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request.MinAmountRequestModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class BankAccountRepoImpl(private val apiService: ApiService) : BankAccountRepo {

    override suspend fun addBankAccount(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.addBankAccount(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_BANK_ACCOUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteBankAccount(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.deleteBankAccount(map)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_BANK_ACCOUNT + "/delete").flowOn(Dispatchers.IO)
    }

    override suspend fun getBankAccountListing(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<BankDetails>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<BankDetails>>> {
                return apiService.getListingOfBankAccount()
            }
        }.safeApiCall(ApiEndPoints.API_GET_BANK_ACCOUNT_LISTING).flowOn(Dispatchers.IO)
    }

    override suspend fun getMaxBankCount(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<MinAmountRequestModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<MinAmountRequestModel>> {
                return apiService.getMaxBankCount()
            }
        }.safeApiCall(ApiEndPoints.API_MA_BANK_COUNT).flowOn(Dispatchers.IO)
    }

    override suspend fun sendBankAccountPrimary(hashMap: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.sendBankAccountPrimaryRequest(hashMap)
            }
        }.safeApiCall(ApiEndPoints.API_MAKE_ACCOUNT_PRIMARY).flowOn(Dispatchers.IO)
    }


}