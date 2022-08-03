package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

interface AllCoursesRepo {
    suspend fun getCourses(
        map: JSONObject

    ): Flow<Resource>

    suspend fun getGuestCourses(
        map: JSONObject
    ): Flow<Resource>

    suspend fun addWishlist(map: Map<String, Any>): Flow<Resource>
    suspend fun purchaseCourse(map: Map<String, Any>): Flow<Resource>
    suspend fun buyRazorPayCourse(map: Map<String, Any>): Flow<Resource>

}