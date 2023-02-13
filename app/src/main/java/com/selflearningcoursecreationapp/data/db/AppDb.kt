package com.selflearningcoursecreationapp.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData

@Database(
    entities = [/*UserEntity::class,*/ OfflineCourseData::class/* OfflineLessonData::class, OfflineSectionData::class*/],
    version = 1
)
@TypeConverters(*[Converters::class])
abstract class AppDb : RoomDatabase() {


    companion object {
        private var INSTANCE: AppDb? = null

        fun getAppDatabase(context: Context): AppDb? {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDb::class.java,
                        "Skillify_DB"
                    )

                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE
        }
    }

    abstract fun getDao(): Dao


}