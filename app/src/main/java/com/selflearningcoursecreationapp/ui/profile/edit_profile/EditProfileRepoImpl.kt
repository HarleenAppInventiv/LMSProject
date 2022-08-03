package com.selflearningcoursecreationapp.ui.profile.edit_profile

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CityModel
import com.selflearningcoursecreationapp.models.StateModel
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class EditProfileRepoImpl(private val apiService: ApiService) : EditProfileRepo {
    override suspend fun updateProfile(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserResponse>> {
                return apiService.updateProfile(map)
            }

        }.safeApiCall(ApiEndPoints.API_UPDATE_PROFILE).flowOn(Dispatchers.IO)
    }

    override suspend fun stateList(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<StateModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<StateModel>>> {
                return apiService.stateList()
            }
        }.safeApiCall(ApiEndPoints.API_GET_ALL_STATES).flowOn(Dispatchers.IO)
    }

    override suspend fun cityList(stateId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<CityModel>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<CityModel>>> {
                return apiService.cityList(stateId)
            }
        }.safeApiCall(ApiEndPoints.API_GET_ALL_CITY).flowOn(Dispatchers.IO)
    }


}