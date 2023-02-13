package com.selflearningcoursecreationapp.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData


object Converters {


    @TypeConverter
    fun fromSectionString(value: String?): ArrayList<OfflineSectionData> {
        val listType = object : TypeToken<ArrayList<OfflineSectionData?>?>() {}.type
        return Gson().fromJson(value, listType)
    }


    @JvmStatic
    @TypeConverter
    fun fromSectionArrayList(list: ArrayList<OfflineSectionData>?): String {

        return Gson().toJson(list)
    }

}