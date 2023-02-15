package com.selflearningcoursecreationapp.extensions


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun Date?.getStringDate(format: String? = "MMM dd, yyyy"): String {
    if (this == null || format.isNullOrEmpty()) {
        return ""
    }

    val outputFormat = SimpleDateFormat(format, Locale.getDefault())
    return outputFormat.format(this)
}

fun String?.changeDateFormat(
    sourceFormat: String? = "yyyy-MM-dd'T'hh:mm:ss",
    outputFormat: String? = "dd MMM yyyy",
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
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    var date = Date()
    try {
        date = inputFormat.parse(this)
    } catch (e: ParseException) {
        showException(e)
    }
//    val localInputFormat = SimpleDateFormat(sourceFormat, Locale.getDefault())
//    localInputFormat.timeZone = TimeZone.getDefault()
//    var time= localInputFormat.format(date.getTime())
//    date= localInputFormat.parse(time)
    return date
}

fun covertTimeToText(dataDate: String?): String? {
    var convTime: String? = null
    val prefix = ""
    val suffix = "Ago"
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val pasTime = dateFormat.parse(dataDate)
        val nowTime = Date()
        val dateDiff = nowTime.time - pasTime.time
        val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
        if (second < 60) {
            convTime = "$second Seconds $suffix"
        } else if (minute < 60) {
            convTime = "$minute Minutes $suffix"
        } else if (hour < 24) {
            convTime = "$hour Hours $suffix"
        } else if (day >= 7) {
            convTime = if (day > 360) {
                (day / 360).toString() + " Years " + suffix
            } else if (day > 30) {
                (day / 30).toString() + " Months " + suffix
            } else {
                (day / 7).toString() + " Week " + suffix
            }
        } else if (day < 7) {
            convTime = "$day Days $suffix"
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return convTime
}

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

fun getCurrentDate(outputFormat: String = "yyyy-MM-dd"): String {
    val sdf = SimpleDateFormat(outputFormat)
    return sdf.format(Date())
}

fun String.convertToUtc(inputFormat: String = "yyyy-MM-dd"): String {
    val selectedDate = this.getFormateedDateYear(inputFormat, "EEE MMM dd")
    val selectedYear = this.getFormateedDateYear(inputFormat, "yyyy")

    val fullDate = "${selectedDate} ${"00:00:00"} ${selectedYear}"

    val sdfForDateUTC = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    sdfForDateUTC.timeZone = TimeZone.getTimeZone("UTC")
    val currentDate = sdfForDateUTC.format(Date(fullDate))

    showLog("searchFields", "" + currentDate)
    return currentDate
}


private fun String.getFormateedDateYear(inFormat: String, outputFormat: String): String {
    val inFormat = SimpleDateFormat(inFormat)
    val date = inFormat.parse(this)
    val outFormat = SimpleDateFormat(outputFormat)
    val goal = outFormat.format(date)
    return goal
}

fun getLastWeekDate(
    dateString: String,
    noOfDays: Int = 7,
    inputFormat: String = "yyyy-MM-dd"
): String {
    val dateFormat = SimpleDateFormat(inputFormat)
    val myDate: Date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = myDate
    calendar.add(Calendar.DAY_OF_YEAR, -noOfDays)
    val newDate = calendar.time
    val date: String = dateFormat.format(newDate)
    return date

}

fun getLastMOnthDate(dateString: String): String {
    val calendar = Calendar.getInstance()
    var monthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return getLastWeekDate(dateString, monthDays)
}

fun getNextMOnthDate(dateString: String, inputFormat: String = "yyyy-MM-dd"): String {
    val dateFormat = SimpleDateFormat(inputFormat)
    val myDate: Date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = myDate
    var monthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return getNextXDays(dateString, monthDays)
}

fun getNextXDays(
    dateString: String,
    noOfDays: Int = 1,
    inputFormat: String = "yyyy-MM-dd"
): String {
    val dateFormat = SimpleDateFormat(inputFormat)
    val myDate: Date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = myDate
    calendar.add(Calendar.DAY_OF_YEAR, +noOfDays)
    val newDate = calendar.time
    val date: String = dateFormat.format(newDate)
    return date
}

fun getNextDay(
    dateString: String,
    sourceFormat: String? = "yyyy-MM-dd",
): String {
    val dateFormat = SimpleDateFormat(sourceFormat)
    val myDate: Date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = myDate
    calendar.add(Calendar.DAY_OF_YEAR, +1)
    val newDate = calendar.time
    val date: String = dateFormat.format(newDate)
    return date
}

fun getLastDay(dateString: String, inputFormat: String = "yyyy-MM-dd"): String {
    val dateFormat = SimpleDateFormat(inputFormat)
    val myDate: Date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = myDate
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val newDate = calendar.time
    val date: String = dateFormat.format(newDate)
    return date
}

fun getTimeAgo(date: String): String {
//    var dateFormat: SimpleDateFormat? = null /*SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")*/
//
//    if (type != 0) {
//        dateFormat = SimpleDateFormat("MM/dd/yyyy 'T'hh:mm:ss")
//
//    } else {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

//    }
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val pasTime = dateFormat.parse(date)

    var time = pasTime.time
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = Calendar.getInstance().timeInMillis
    if (time > now || time <= 0) {
        return "Just Now"
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "moments ago"
        diff < 2 * MINUTE_MILLIS -> "a mins ago"
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} mins ago"
        diff < 2 * HOUR_MILLIS -> "an hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}

//fun getLocalToUTCDate(date: Date?): String? {
//    val calendar: Calendar = Calendar.getInstance()
//    calendar.time = date
//    calendar.timeZone = TimeZone.getTimeZone("UTC")
//    val time: Date = calendar.time
//    @SuppressLint("SimpleDateFormat") val outputFmt =
//        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//    outputFmt.timeZone = TimeZone.getTimeZone("UTC")
//    return outputFmt.format(time)
//}


fun getTime(date: Long): String {
//    var dateFormat: SimpleDateFormat? = null /*SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")*/
//
//    if (type != 0) {
//        dateFormat = SimpleDateFormat("MM/dd/yyyy 'T'hh:mm:ss")
//
//    } else {

//    }

    var time = date
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return "Just Now"
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "seconds"
        diff < 2 * MINUTE_MILLIS -> " mins "
        diff < 2 * HOUR_MILLIS -> " hour "
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "yesterday"
        else -> "${diff / DAY_MILLIS} days ago"
    }
}


fun increaseDecrease(isIncrement: Boolean, givenDate: Date): Date {
    return if (!isIncrement) {
        val c = Calendar.getInstance()
        c.time = givenDate
        c.add(Calendar.MONTH, -1)
        c.time
    } else {
        val c = Calendar.getInstance()
        c.time = givenDate
        c.add(Calendar.MONTH, 1)
        c.time

    }

}

fun getFirstLastDateOfMonth(isFirst: Boolean, date: Date?): Date? {
    val cal = Calendar.getInstance()
    cal.time = date
    if (isFirst) {
        cal[Calendar.DAY_OF_MONTH] = cal.getActualMinimum(Calendar.DAY_OF_MONTH)

    } else {
        cal[Calendar.DAY_OF_MONTH] = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    }
    return cal.time
}

fun LocalToGMT(date: Date): String {
//    val date = Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(date)
}