package com.selflearningcoursecreationapp.extensions

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R


fun Activity.hideKeyboard(): Boolean {
    try {
        if (currentFocus != null) {
            val iMM = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            iMM?.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
            return true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun View.hideKeyboard() {
    val iMM = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    iMM?.hideSoftInputFromWindow(this.windowToken, 0)

}

fun Context.getAttrColor(attrColor: Int): Int {
    val typedValue = TypedValue()
    val theme = theme
    theme.resolveAttribute(attrColor, typedValue, true)
    return typedValue.resourceId
}

fun TextView.setColor(color: Int) {
    setTextColor(ContextCompat.getColor(context, context.getAttrColor(color)))
}


fun showLog(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun showException(e: java.lang.Exception) {
    if (BuildConfig.DEBUG) {
        e.printStackTrace()
    }
}


fun TextView.setSpanString(spanText: SpannableString?) {
    text = spanText
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(context, android.R.color.transparent)
}


fun <T> String?.getPojoData(type: Class<T>): T? {

    if (this.isNullOrEmpty()) {
        return null
    } else
        return Gson().fromJson<T>(this, type)
}


fun Context.getTime(milliseconds: Long?): String {
    if (milliseconds.isNullOrZero()) {
        return ""
    }


    val seconds = milliseconds!!.div(1000).rem(60).toInt()
    val mins = milliseconds!!.div(1000.times(60)).rem(60).toInt()
    val hrs = milliseconds!!.div(1000.times(60).times(60)).rem(24).toInt()

    var time =
        "${if (hrs > 9) hrs else "0$hrs"}:${if (mins > 9) mins else "0$mins"}:${if (seconds > 9) seconds else "0$seconds"}"

    return time

}

fun Context.getTimeInChar(milliseconds: Long?): String {
    if (milliseconds.isNullOrZero()) {
        return ""
    }


    val seconds = milliseconds!!.div(1000).rem(60).toInt()
    val mins = milliseconds!!.div(1000.times(60)).rem(60).toInt()
    val hrs = milliseconds!!.div(1000.times(60).times(60)).rem(24).toInt()
    var time = ""

    if (hrs > 0) {
        time = getQuantityString(R.plurals.hour_quantity, hrs!!)
    }
    if (mins > 0) {
        time = "$time ${getQuantityString(R.plurals.min_quantity_small, mins!!)}"
    }
    if (seconds > 0) {
        time = "$time ${getQuantityString(R.plurals.sec_quantity, seconds!!)}"
        }

    return if (time.startsWith(",")) time.substring(1) else time

}