package com.selflearningcoursecreationapp.ui.content_creator.course_detail


import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface RevenueFragmentRepo {


    suspend fun getRevenueList(map: HashMap<String, Any>): Flow<Resource>


}