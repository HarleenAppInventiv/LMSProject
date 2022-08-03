package com.selflearningcoursecreationapp.extensions

import androidx.databinding.InverseMethod
import java.util.concurrent.TimeUnit

object BindingConverters {

    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(value: Int): String {
        return if (value.isNullOrZero()) "" else value.toString()
    }

    @JvmStatic
    fun stringToInt(value: String): Int {
        return if (value.isEmpty()) 0 else value.toIntOrNull() ?: 0
    }

    @JvmStatic
    fun minutesToMillis(value: String): Long {
        return if (value.isEmpty()) 0 else TimeUnit.MINUTES.toMillis(value?.toLongOrNull() ?: 0L)
    }

    @InverseMethod("minutesToMillis")
    @JvmStatic
    fun millisToMinutes(value: Long): String {
        return if (value.isNullOrZero()) "" else value/*?.div(10000)*/?.div(60000)?.toString() ?: ""
    }
}