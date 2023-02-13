package com.selflearningcoursecreationapp.ui.moderator.myCategories

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class MyCategoriesRepoImpl(private val apiService: ApiService) : MyCategoriesRepo {

    override suspend fun myCategories(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ModeratorMyCategories>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ModeratorMyCategories>> {
                return apiService.modMyCategories()
            }
        }.safeApiCall(ApiEndPoints.API_MOD_MY_CATEGORIES).flowOn(Dispatchers.IO)
    }

}