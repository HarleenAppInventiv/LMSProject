package com.selflearningcoursecreationapp.extensions


import android.content.Context
import android.view.View
import com.selflearningcoursecreationapp.R


fun View.visibleView(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else View.GONE
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

fun Double?.isNullOrZero(): Boolean {
    return this == null || this == 0.0
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
    return quizTypeArray[type - 1]
}




