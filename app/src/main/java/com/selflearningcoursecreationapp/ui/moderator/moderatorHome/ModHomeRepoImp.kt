package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.moderator.ModeratorDataSource

class ModHomeRepoImp(private val apiService: ApiService) : ModHomeRepo {
    override suspend fun courseRequest(map: HashMap<String, Int>): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ModeratorDataSource(apiService, map, type = "course_req")
            }
        ).liveData
    }

    override suspend fun modCourse(map: HashMap<String, Int>): LiveData<PagingData<CourseData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ModeratorDataSource(apiService, map, type = "mod_Course")
            }
        ).liveData
    }

}