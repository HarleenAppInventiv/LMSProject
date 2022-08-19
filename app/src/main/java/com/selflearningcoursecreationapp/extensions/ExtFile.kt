package com.selflearningcoursecreationapp.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.gson.Gson
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

    return if (this.isNullOrEmpty()) {
        null
    } else
        Gson().fromJson(this, type)
}


fun Long?.getTime(context: Context, showHms: Boolean = true, showHrs: Boolean = true): String {
    if (this.isNullOrZero()) {
        return ""
    } else if (showHms) {
        return getTimeInHMS(showHrs)
    } else {
        return context.getTimeInChar(this)
    }


}

private fun Long?.getTimeInHMS(showHrs: Boolean): String {
    val seconds = this!!.div(1000).rem(60).toInt()
    val mins = this.div(1000.times(60)).rem(60).toInt()
    val hrs = this.div(1000.times(60).times(60)).rem(24).toInt()




    return "${if (hrs == 0 && !showHrs) "" else if (hrs > 9) hrs.toString() + ":" else "0$hrs:"}${if (mins > 9) mins else "0$mins"}:${if (seconds > 9) seconds else "0$seconds"}"

}

fun Context.getTimeInChar(milliseconds: Long?): String {
    if (milliseconds.isNullOrZero()) {
        return ""
    }


    val seconds = milliseconds!!.div(1000).rem(60).toInt()
    val mins = milliseconds.div(1000.times(60)).rem(60).toInt()
    val hrs = milliseconds.div(1000.times(60).times(60)).rem(24).toInt()

    var time = ""
    if (hrs > 0) {
        time = if (hrs > 9) hrs.toString() else "0$hrs"

    }
    if (mins > 0) {
        time += ".${if (mins > 9) mins else "0$mins"}"

    }
    if (seconds > 0 && (time.isEmpty() || time.startsWith("."))) {
        time += ".${if (seconds > 9) seconds else "0$seconds"}"
    }

    val format = if (hrs > 0) {
        "hrs"
    } else if (mins > 0) {
        "mins"
    } else {

        "secs"
    }
    return String.format("%s %s", if (time.startsWith(".")) time.substring(1) else time, format)



}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}
fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}
fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = Math.round(this.convertDpToPx(50F))
    return heightDiff > marginOfError
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

fun View?.fitSystemWindowsAndAdjustResize() = this?.let { view ->
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        view.fitsSystemWindows = true
        val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

        WindowInsetsCompat
            .Builder()
            .setInsets(
                WindowInsetsCompat.Type.systemBars(),
                Insets.of(0, 0, 0, bottom)
            )
            .build()
            .apply {
                ViewCompat.onApplyWindowInsets(v, this)
            }
    }
}

fun NavController.navigateTo(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    try {

        navigate(resId, args, navOptions, navigatorExtras)
    } catch (e: Exception) {
        showException(e)
    }
}