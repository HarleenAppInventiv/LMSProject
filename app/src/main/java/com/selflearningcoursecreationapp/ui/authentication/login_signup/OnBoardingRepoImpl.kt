package com.selflearningcoursecreationapp.ui.authentication.login_signup

import ccom.example.roomwithmvvmdemo.all_states.StateData
import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class OnBoardingRepoImpl(private val apiService: ApiService) : OnBoardingRepo {


    override suspend fun allStates(): Flow<Resource> {
        return object : BaseRepo<StateData>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<StateData>> {
                return apiService.getAllStates()
            }

        }.safeApiCall(ApiEndPoints.API_COUNTRIES).flowOn(Dispatchers.IO)



    }
}