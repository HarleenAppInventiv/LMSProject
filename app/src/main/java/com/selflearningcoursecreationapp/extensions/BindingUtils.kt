package com.selflearningcoursecreationapp.extensions

import android.text.SpannableString
import android.text.style.LocaleSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
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
    if (typeSecondary) {
        setSecondaryBtnDisabled(value)

    } else {
        setBtnDisabled(value, typeSecondary)
    }
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

@BindingAdapter(value = ["htmlString", "count"], requireAll = false)
fun TextView.setLimitedText(data: String?, lines: Int? = 9) {
    if (data.isNullOrEmpty()) {
        text = ""
    } else {
        setSpanString(SpannableString(data))

        var textCount = lineCount
        if (textCount > (lines ?: 9)) {
            textCount = lines ?: 9


            val linesList: ArrayList<CharSequence> = ArrayList()

            for (i in 0 until textCount) {
                val start = layout.getLineStart(i)
                val end = layout.getLineEnd(i)
                val substring: CharSequence = text.subSequence(start, end)
                linesList.add(substring.toString())
            }
            linesList.add("...")
            text = linesList.joinToString(" ")
        }
    }


}

@BindingAdapter(value = ["wordLimit"], requireAll = false)
fun EditText.setWordLimit(limit: Int = 100) {


    doOnTextChanged { input, _, _, _ ->
        if (!input.isNullOrEmpty()) {
            val list = input.toString().split(" ")
//            var count = 0
//            for (i in 0 until list.size)
//            {
//                if (i<100)
//                {
//                    count+=list[i].length
//                    count+=1
//                }else{
//                    break
//                }
//            }
            if (list.size > limit) {
                setText(list.dropLast(list.size - limit).joinToString(" ").toString())
                setSelection(text.length)
            }
//            val newText= data?.substring(0,count)

        }
    }


}

