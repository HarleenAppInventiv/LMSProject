package com.selflearningcoursecreationapp.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Date?.getStringDate(format: String? = "MMM dd, yyyy"): String {
    if (this == null || format.isNullOrEmpty()) {
        return ""
    }
    val outputFormat = SimpleDateFormat(format, Locale.getDefault())
    return outputFormat.format(this)
}

fun String?.changeDateFormat(
    sourceFormat: String? = "yyyy-MM-dd'T'HH:mm:ss",
    outputFormat: String? = "MMM dd, yyyy"
): String {
    if (isNullOrEmpty()) {
        return ""
    }
    return createDate(sourceFormat).getStringDate(outputFormat)
}

fun String?.createDate(sourceFormat: String?): Date {
    if (isNullOrEmpty()) {
        return Date()
    }
    val inputFormat = SimpleDateFormat(sourceFormat, Locale.getDefault())
    var date = Date()
    try {
        date = inputFormat.parse(this)
    } catch (e: ParseException) {
        showException(e)
    }
    return date
}