package com.selflearningcoursecreationapp.data.db

import androidx.room.*
import androidx.room.Dao
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData


@Dao
interface Dao {

    @Query("DELETE FROM offlinecoursedata")
    fun nukeTable()

    @Query("SELECT * FROM offlinecoursedata WHERE user_id = :userId ORDER BY id DESC")
    suspend fun getAllCourses(userId: Int): List<OfflineCourseData>?

    @Query("SELECT * FROM offlinecoursedata  ORDER BY id DESC")
    suspend fun getAllCourses(): List<OfflineCourseData>?

    @Query("SELECT * FROM offlinecoursedata WHERE course_id = :courseId AND user_id = :userId ORDER BY id DESC")
    suspend fun getCourse(courseId: Int, userId: Int): OfflineCourseData?

    @Insert
    suspend fun insertCourseData(user: OfflineCourseData?): Long

    @Delete
    suspend fun deleteCourse(user: OfflineCourseData?)

    @Update
    suspend fun updateCourse(user: OfflineCourseData?)


}