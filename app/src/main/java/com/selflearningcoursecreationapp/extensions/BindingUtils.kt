package com.selflearningcoursecreationapp.extensions

import android.text.SpannableString
import android.text.style.LocaleSpan
import android.view.View
import android.widget.EditText
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

@BindingAdapter(value = ["btnEnabled", "typeSecondary"], requireAll = false)
fun LMSMaterialButton.setBtnEnabled(value: Boolean, typeSecondary: Boolean = false) {
    showLog("BUTTTON_DISABLED", "${text} >> ${typeSecondary}")
    setBtnDisabled(value, typeSecondary)
}

@BindingAdapter("doEnable")
fun TextView.doEnable(isEnable: Boolean) {
    isClickable = isEnable
    isFocusableInTouchMode = isEnable
    isFocusable = isEnable
    isEnabled = isEnable

}

@BindingAdapter("doEnable")
fun View.doEnable(isEnable: Boolean) {
    isClickable = isEnable
    isFocusableInTouchMode = isEnable
    isFocusable = isEnable
    isEnabled = isEnable

}

@BindingAdapter("doEnable")
fun EditText.doEnable(isEnable: Boolean) {
    isClickable = isEnable
    isFocusableInTouchMode = isEnable
    isFocusable = isEnable
    isEnabled = isEnable

}


