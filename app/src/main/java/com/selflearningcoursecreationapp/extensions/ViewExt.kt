package com.selflearningcoursecreationapp.extensions


import android.content.Context
import android.view.View
import com.selflearningcoursecreationapp.R


fun View.visibleView(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else View.GONE
}

fun View.inVisibleView(isShow: Boolean) {
    visibility = if (isShow) {
        View.VISIBLE
    } else View.INVISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Int?.isNullOrZero(): Boolean {
    return this == null || this == 0
}

fun Long?.isNullOrZero(): Boolean {
    return this == null || this == 0L
}

fun Int?.isNullOrNegative(): Boolean {
    return this == null || this == -1
}

fun Double?.isNullOrZero(): Boolean {
    return this == null || this == 0.0
}

fun Int?.getCharString(): String {
    if (this == null)
        return ""
    return (this + 65).toChar().toString()
}

fun String.wordCount(): Int {
    val trimmedStr = trim()
    return if (trimmedStr.isEmpty()) {
        0
    } else {
        trimmedStr.split("\\s+".toRegex()).size
    }
}

fun Context.getQuizTypeTitle(type: Int): String {
    val quizTypeArray = resources.getStringArray(R.array.quiz_option_array)
    return quizTypeArray[if (type == 0) 0 else (type - 1)]
}

fun Context.getQuantityString(resId: Int, quantity: Int?): String {
    return resources.getQuantityString(
        resId,
        quantity ?: 0,
        quantity ?: 0
    )
}




