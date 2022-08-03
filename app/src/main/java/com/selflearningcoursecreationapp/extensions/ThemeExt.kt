package com.selflearningcoursecreationapp.extensions

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

fun BottomNavigationView.setNavTint(){
    val states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked),
    )
    val colors = intArrayOf(
        ThemeUtils.getAppColor(context),
        ContextCompat.getColor(context, com.selflearningcoursecreationapp.R.color.hint_color_929292)
    )
    itemIconTintList= ColorStateList(states, colors)
}

fun ExtendedFloatingActionButton.setThemeTint() {
    backgroundTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))

}

