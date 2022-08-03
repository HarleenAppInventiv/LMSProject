package com.selflearningcoursecreationapp.ui.bottom_home.categories

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

class HomeCategoryRepoImpl(private var apiService: ApiService) : HomeCategoryRepo {
    //    override suspend fun getCategories(): Flow<Resource> {
//        return object : BaseRepo<BaseResponse<HomeAllCategoryResponse>>() {
//            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<HomeAllCategoryResponse>> {
//                return apiService.getHomeAllCategories()
//            }
//        }.safeApiCall(ApiEndPoints.API_HOME_ALL_CATEGORIES).flowOn(Dispatchers.IO)
//
//    }
    override suspend fun getCategories(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<CategoryResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<CategoryResponse>> {
                return apiService.getCategories()
            }
        }.safeApiCall(ApiEndPoints.API_GET_CATEGORIES).flowOn(Dispatchers.IO)

    }
}