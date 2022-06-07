package com.selflearningcoursecreationapp.extensions

import androidx.databinding.InverseMethod

object BindingConverters {

    @InverseMethod("stringToInt")
    @JvmStatic
    fun intToString(value: Int): String {
        return if (value.isNullOrZero()) "" else value.toString()
    }

    @JvmStatic
    fun stringToInt(value: String): Int {
        return if (value.isNullOrEmpty()) 0 else value.toIntOrNull() ?: 0
    }
}