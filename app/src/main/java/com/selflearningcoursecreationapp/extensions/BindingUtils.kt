package com.selflearningcoursecreationapp.extensions

import android.text.SpannableString
import android.text.style.LocaleSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.selflearningcoursecreationapp.utils.customViews.LMSMaterialButton
import java.util.*


@BindingAdapter("spanText")
fun TextView.setSpanText(txt: String?) {
    val spannable = SpannableString(txt)
    spannable.setSpan(LocaleSpan(Locale.getDefault()), 0, spannable.length, 0)
    text = spannable
}

@BindingAdapter("btnEnabled")
fun LMSMaterialButton.setBtnEnabled(value: Boolean) {
    setBtnDisabled(value)


}

@BindingAdapter("doEnable")
fun TextView.doEnable(isEnable: Boolean) {
    isClickable = isEnable
    isFocusableInTouchMode = isEnable
    isFocusable = isEnable
    isEnabled = isEnable

}


