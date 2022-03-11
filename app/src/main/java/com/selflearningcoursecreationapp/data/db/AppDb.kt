package com.selflearningcoursecreationapp.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {

    abstract fun userDao(): Dao?

    companion object {
        private var INSTANCE: AppDb? = null

        fun getAppDatabase(context: Context): AppDb? {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context.applicationContext, AppDb::class.java, "AppDb")
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE
        }
    }


}