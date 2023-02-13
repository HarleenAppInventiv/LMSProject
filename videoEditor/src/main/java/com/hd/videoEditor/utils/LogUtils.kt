package com.hd.videoEditor.utils

import android.util.Log
import org.koin.android.BuildConfig


fun Logv(tag: String = "HD_EDITOR", msg: String? = "") {
    if (BuildConfig.DEBUG) {
        Log.v(tag, msg ?: "no log to display")
    }
}

fun Loge(tag: String = "HD_EDITOR", msg: String? = "") {
//    if (BuildConfig.DEBUG){
    Log.e(tag, msg ?: "no log to display")
//}
}

fun Logd(tag: String = "HD_EDITOR", msg: String? = "") {
//    if (BuildConfig.DEBUG){
    Log.d(tag, msg ?: "no log to display")
//}
}

fun Logi(tag: String = "HD_EDITOR", msg: String? = "") {
    if (BuildConfig.DEBUG) {
        Log.i(tag, msg ?: "no log to display")
    }
}

fun Logw(tag: String = "HD_EDITOR", msg: String? = "") {
    if (BuildConfig.DEBUG) {
        Log.w(tag, msg ?: "no log to display")
    }
}

fun showException(e: Exception) {
//    if (BuildConfig.DEBUG) {
    e.printStackTrace()
//    }
}




