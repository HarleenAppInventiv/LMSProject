package com.selflearningcoursecreationapp.utils

import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.TouchDelegate
import android.view.View
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.showLog

fun isInternetAvailable(context: Context? = SelfLearningApplication.applicationContext()): Boolean {
    val connectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        nwInfo.isConnected
    }
}

fun isLessThan9(value: Int): String {
    return if (value < 10) "0${value}" else value.toString()
}

fun increaseTouch(view: View, value: Float) {
    val parent = view.parent
    (parent as View).post {
        val rect = Rect()
        view.getHitRect(rect)
        val intValue = value.toInt()
        rect.top -= intValue    // increase top hit area
        rect.left -= intValue   // increase left hit area
        rect.bottom += intValue // increase bottom hit area
        rect.right += intValue  // increase right hit area
        parent.touchDelegate = TouchDelegate(rect, view)
    }
}

fun Float.convertPaiseToRs(): String {
    var rupees = 0f

    if (this > 0f)
        rupees = this / 100f
    return rupees.toString()
}

fun String.getDotsAfterTwentyChars(): String {
    var str = this

    if (str.length > 20) {
        str = "${this.substring(0, 20)}..."
    }
    return str
}


fun checkUnclosedTags(htmlString: String?, completeString: String): String {
    var mainString = htmlString
    var startTagList = ArrayList<String>()
    var endTagList = ArrayList<String>()
    showLog("TEXT_STRING", "html string >>${htmlString} ")
    var tag = ""
    htmlString?.forEachIndexed { index, c ->
        if (c.toString().equals("<")) {
            tag = "<"
        } else if (c.toString().equals(">")) {
            tag += c
            if (tag.contains("/")) {
                endTagList.add(tag)
            } else {
                startTagList.add(tag)
            }
            showLog("TEXT_STRING", "tag value >> $tag")
            tag = ""
        } else if (tag.isNotEmpty()) {
            tag += c
        }


    }

    if (tag.isNotEmpty()) {
        mainString = mainString?.removeSuffix(tag)
    }

    showLog("TEXT_STRING", "tag list >>${startTagList.size} ")
    showLog("TEXT_STRING", "end tag list >>${endTagList.size} ")

    if (startTagList.size != endTagList.size) {
        startTagList.forEachIndexed { index, s ->
            if (index <= endTagList.size) {
                mainString += "</" + s.substringAfter("<")

            }
        }
//            startTagList.subList(endTagList.size, startTagList.size-1)?.forEach { tag->
//                mainString+= "</"+tag.substringAfter("<")
//
//            }
    }
    showLog("TEXT_STRING", "mainString >>${mainString} ")
    return mainString ?: ""
}





