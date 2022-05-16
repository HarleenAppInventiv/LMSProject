package com.selflearningcoursecreationapp.ui.preferences

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CategoryResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class PreferenceRepoImpl(private val apiService: ApiService) : PreferenceRepo {
    override suspend fun savePreference(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.savePrefrence(map)
            }

        }.safeApiCall(ApiEndPoints.API_SAVE_PREFRENCE).flowOn(Dispatchers.IO)
    }

    override suspend fun getCategory(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CategoryResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CategoryResponse>> {
                return apiService.getCategories()
            }

        }.safeApiCall(ApiEndPoints.API_GET_CATEGORIES).flowOn(Dispatchers.IO)
    }

    override suspend fun myCategories(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CategoryResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CategoryResponse>> {
                return apiService.myCategories()
            }

        }.safeApiCall(ApiEndPoints.API_MYCATEGORIES).flowOn(Dispatchers.IO)
    }

    override suspend fun getThemeList(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CategoryResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CategoryResponse>> {
                return apiService.getThemeList()
            }

        }.safeApiCall(ApiEndPoints.API_GETTHEME_LIST).flowOn(Dispatchers.IO)
    }

}