package com.selflearningcoursecreationapp.extensions


import android.view.View


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





