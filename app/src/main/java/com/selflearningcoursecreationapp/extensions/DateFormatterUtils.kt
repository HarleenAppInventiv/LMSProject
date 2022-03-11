package com.selflearningcoursecreationapp.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date?.getStringDate(format:String?="MMM dd, yyyy"):String
{
    if (this == null ||format.isNullOrEmpty())
    {return ""
    }
    val outputFormat= SimpleDateFormat(format, Locale.getDefault())
    return outputFormat.format(this)
}