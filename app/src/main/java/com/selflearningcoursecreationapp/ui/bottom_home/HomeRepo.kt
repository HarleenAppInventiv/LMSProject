package com.selflearningcoursecreationapp.ui.bottom_home

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

interface HomeRepo {
    suspend fun homeCourse(map: JSONObject): Flow<Resource>
    suspend fun guestHomeCourse(map: JSONObject): Flow<Resource>
    suspend fun homeCategories(): Flow<Resource>
    suspend fun addWishlist(map: Map<String, Any>, fromWishlist: Boolean): Flow<Resource>
    suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource>

    suspend fun getOtherCourses(
        map: JSONObject
    ): Flow<Resource>

    suspend fun getGuestOtherCourses(
        map: JSONObject
    ): Flow<Resource>


}