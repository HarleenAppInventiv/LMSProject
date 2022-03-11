package com.selflearningcoursecreationapp.data.db

import androidx.room.*
import androidx.room.Dao


@Dao
interface Dao {
    @Query("SELECT * FROM userinfo ORDER BY id DESC")
    fun getAllUserInfo(): List<UserEntity>?

    @Insert
    fun insertUser(user: UserEntity?)

    @Delete
    fun dleteUser(user: UserEntity?)

    @Update
    fun updateUser(user: UserEntity?)
}