package com.selflearningcoursecreationapp.extensions

import android.content.res.ColorStateList
import android.text.SpannableString
import android.text.style.LocaleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.customViews.LMSEditText
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import java.util.*


@BindingAdapter("spanText")
fun TextView.setSpanText(txt:String?){
    val spannable = SpannableString(txt)
    spannable.setSpan(LocaleSpan(Locale.getDefault()), 0, spannable.length, 0)
   text=spannable
}



