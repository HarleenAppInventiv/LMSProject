package com.selflearningcoursecreationapp.ui.search


import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class SearchFragmentRepoImp(private val apiService: ApiService) : SearchFragmentRepo {

    override suspend fun elasticSearch(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ElasticSearchData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ElasticSearchData>> {
                return apiService.getElasticSearchData(map)
            }
        }.safeApiCall(ApiEndPoints.API_HOME_SEARCH).flowOn(Dispatchers.IO)
    }


}