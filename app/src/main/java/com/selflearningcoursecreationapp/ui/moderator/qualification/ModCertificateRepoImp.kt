package com.selflearningcoursecreationapp.ui.moderator.qualification

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.DocModelItem
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class ModCertificateRepoImp(private val apiService: ApiService) : ModCertificateRepo {
    override suspend fun myDocs(): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ArrayList<DocModelItem>>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ArrayList<DocModelItem>>> {
                return apiService.myDocs()
            }
        }.safeApiCall(ApiEndPoints.API_DOC_DOWNLOAD).flowOn(Dispatchers.IO)
    }

}