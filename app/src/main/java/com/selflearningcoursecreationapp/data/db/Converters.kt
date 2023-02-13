package com.selflearningcoursecreationapp.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData


object Converters {


//    @TypeConverter
//    fun fromString(value: String?): ArrayList<OfflineLessonData> {
//        val listType = object : TypeToken<ArrayList<String?>?>() {}.type
//        return Gson().fromJson(value, listType)
//    }
//
//
//    @JvmStatic
//    @TypeConverter
//    fun fromArrayList(list: ArrayList<OfflineLessonData>?): String {
//        return Gson().toJson(list)
//    }

    @TypeConverter
    fun fromSectionString(value: String?): ArrayList<OfflineSectionData> {
        val listType = object : TypeToken<ArrayList<OfflineSectionData?>?>() {}.type
        return Gson().fromJson(value, listType)
    }


    @JvmStatic
    @TypeConverter
    fun fromSectionArrayList(list: ArrayList<OfflineSectionData>?): String {
//        val type = object: TypeToken<ArrayList<OfflineSectionData>>() {}.type

        return Gson().toJson(list)
    }

}