package com.selflearningcoursecreationapp.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.LocaleSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import java.util.*


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


fun Context.getSpanString(
    message: String?,
    color: Int? = ThemeUtils.getPrimaryTextColor(this),
    attrColor: Int = R.attr.primaryTextColor,
    startPos: Int = 0,
    isBold: Boolean = false,
    isUnderline: Boolean = false,
    endPos: Int = message?.length ?: 0,
    onClick: () -> Unit = {}
): SpannableString {

    val ss = SpannableString(message)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(p0: View) {
            onClick()
        }

        @SuppressLint("ResourceType")
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = isUnderline
            if (color.isNullOrZero()) {
                val typedValue = TypedValue()
                val theme = theme
                theme.resolveAttribute(attrColor, typedValue, true)
                ds.color = ContextCompat.getColor(this@getSpanString, typedValue.resourceId)
            } else {
                ds.color = color!!

            }


            if (isBold) {
                ds.typeface = Typeface.DEFAULT_BOLD
            }
        }
    }

//    if (isBold) {
//        ss.setSpan(StyleSpan(Typeface.BOLD), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//    }
    ss.setSpan(LocaleSpan(Locale.getDefault()), 0, ss.length, 0)

    ss.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ss
}


fun showLog(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun showException( e: java.lang.Exception) {
    if (BuildConfig.DEBUG) {
      e.printStackTrace()
    }
}

