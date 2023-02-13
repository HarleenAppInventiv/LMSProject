package com.selflearningcoursecreationapp.ui.search


import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface SearchFragmentRepo {

    suspend fun elasticSearch(map: HashMap<String, Any>): Flow<Resource>

}