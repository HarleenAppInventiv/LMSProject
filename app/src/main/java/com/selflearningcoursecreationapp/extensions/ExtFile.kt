package com.selflearningcoursecreationapp.extensions

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.BuildConfig


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
