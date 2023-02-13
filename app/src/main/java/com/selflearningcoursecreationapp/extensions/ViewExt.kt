package com.selflearningcoursecreationapp.extensions


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.BindingAdapter
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

@BindingAdapter("visibleView")
fun View.visibleView(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else View.GONE
}


//@BindingAdapter("changeColorsOfLikeDislike")
//fun LMSImageView.changeColorsOfTheView(type: Int) {
//    if (type==1) {
//        this.changeBackground(ThemeConstants.TYPE_THEME)
//    } else{
//
//        this.imageTintList = null
//
//    }
//}

fun sad(view: View, motionLayout: MotionLayout) {
    motionLayout.getConstraintSet(R.id.mode)
        .getConstraint(view.id).propertySet.mVisibilityMode =
        1 // 1 - ignore or 0 - normal

    view.visibility = View.GONE
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

fun Long?.isNullOrNegative(): Boolean {
    return this == null || this < 0L
}

fun Int?.isNullOrNegative(): Boolean {
    return this == null || this == -1
}

fun Double?.isNullOrZero(): Boolean {
    return this == null || this == 0.0
}

fun Double?.isNullOrNegative(): Boolean {
    return this == null || this == -1.0
}

fun Int?.getCharString(): String {
    if (this == null)
        return ""
    return (this + 65).toChar().toString()
}

fun Int?.milliSecToMin(): Int {
    if (this == null)
        return 0
    return (this / 1000 / 60)
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


fun ProgressBar.applyPrimaryTint() {
    progressTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
    progressTintMode = PorterDuff.Mode.SRC_IN
    indeterminateTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
    indeterminateTintMode = PorterDuff.Mode.SRC_IN

}





