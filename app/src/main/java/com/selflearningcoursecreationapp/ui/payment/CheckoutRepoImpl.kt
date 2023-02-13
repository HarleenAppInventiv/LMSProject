package com.selflearningcoursecreationapp.ui.payment

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class CheckoutRepoImpl(private val apiService: ApiService) : CheckoutRepo {

    override suspend fun getAmountWithGst(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AmountWithGSTModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AmountWithGSTModel>> {
                return apiService.getAmountWithGst(map)
            }
        }.safeApiCall(ApiEndPoints.API_AMOUNT_WITH_GST).flowOn(Dispatchers.IO)
    }

}