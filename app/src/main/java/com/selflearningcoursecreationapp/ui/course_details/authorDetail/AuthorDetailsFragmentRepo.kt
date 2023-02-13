package com.selflearningcoursecreationapp.ui.course_details.authorDetail


import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface AuthorDetailsFragmentRepo {


    suspend fun getAuthorDetails(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getCoAuthorDetails(map: HashMap<String, Any>): Flow<Resource>
    suspend fun addWishlist(map: HashMap<String, Any>, fromWishlist: Boolean): Flow<Resource>
    suspend fun purchaseCourse(map: HashMap<String, Any>): Flow<Resource>
    suspend fun buyRazorPayCourse(map: HashMap<String, Any>): Flow<Resource>


}