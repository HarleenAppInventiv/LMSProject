package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.selflearningcoursecreationapp.models.course.CourseData

interface ModHomeRepo {
    suspend fun courseRequest(
        map: HashMap<String, Int>
    ): LiveData<PagingData<CourseData>>

    suspend fun modCourse(
        map: HashMap<String, Int>
    ): LiveData<PagingData<CourseData>>
}