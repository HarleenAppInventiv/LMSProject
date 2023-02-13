package com.selflearningcoursecreationapp.extensions

import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.style.LocaleSpan
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.textEditor.TextEditor
import com.selflearningcoursecreationapp.utils.ComplexityLevel
import com.selflearningcoursecreationapp.utils.customViews.*
import java.util.*


@BindingAdapter("spanText")
fun TextView.setSpanText(txt: String?) {
    val spannable = SpannableString(txt)
    spannable.setSpan(LocaleSpan(Locale.getDefault()), 0, spannable.length, 0)
    text = spannable
}


@BindingAdapter("changeBookmarkBg")
fun LMSImageView.changeBookmarkBg(changeBg: Int) {
    if (changeBg == 0) {
        setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.transBlack
            )
        )
    } else {
        this.changeBackground(ThemeConstants.TYPE_BACKGROUND)
    }
}

@BindingAdapter("changeBookmarkBg")
fun LMSShapeAbleImageView.changeBookmarkBg(changeBg: Int) {
    if (changeBg == 0) {
        setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.transBlack
            )
        )
    } else {
        this.changeBgColor(ThemeConstants.TYPE_BACKGROUND)
    }
}

@BindingAdapter(value = ["btnEnabled", "typeSecondary"], requireAll = false)
fun LMSMaterialButton.setBtnEnabled(value: Boolean, typeSecondary: Boolean = false) {
    if (typeSecondary) {
        setSecondaryBtnDisabled(value)

    } else {
        setBtnDisabled(value)
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

@BindingAdapter("doClickAble")
fun View.doClickAble(isEnable: Boolean) {
    isClickable = isEnable
    isEnabled = isEnable

}

@BindingAdapter("doEnable")
fun EditText.doEnable(isEnable: Boolean) {
    isClickable = isEnable
    isFocusableInTouchMode = isEnable
    isFocusable = isEnable
    isEnabled = isEnable

}


@BindingAdapter(value = ["reviewCount", "showBrackets"], requireAll = false)
fun TextView.reviewCount(reviewCount: Long, showBrackets: Boolean? = true) {
    text = reviewCount.getReviewCount(showBrackets ?: true)

}


@BindingAdapter(value = ["formattedString"])
fun TextEditor.setFormattedText(unformattedText: String?) {
    if (!unformattedText.isNullOrEmpty()) {
        html = unformattedText + ""

    }
}


//@BindingAdapter(value = ["formattedStringDesc"], requireAll = false)
//fun WebView.setFormattedTextDesc(unformattedText: String?) {
//
//    if(unformattedText?.trim()?.replace("<p><br></p>", "\n")?.length ?: 0 >150)
//    {
//        val newT= checkUnclosedTags(unformattedText?.trim()?.replace("<p><br></p>", "\n")?.substring(0,150)+"...")
////        this.setFormattedText(newT)
//        displayDataToWeb(newT, this)
//
//    }else {
////        this.setFormattedText(unformattedText)
//        unformattedText?.let { displayDataToWeb(it,this) }
//    }
//
//
//}

@BindingAdapter(value = ["formattedStringDesc", "lineCount"], requireAll = false)
fun WebView.setFormattedTextDesc(unformattedText: String?, lineCount: Int = 3) {

//    var desc= "asadasadasadasd<br>a<br>a<br>a<br>a<br>a<br>a<br>aa<br>a<br>"
//            var desc = courseData.courseDescription ?: ""
    var readMoreStatus = getMoreText(unformattedText.toString())
    if (readMoreStatus.first) {
        displayDataToWeb(readMoreStatus.second, this)
    } else {
        displayDataToWeb(unformattedText.toString(), this)
    }

}

@BindingAdapter(value = ["formattedString"], requireAll = false)
fun WebView.setFormattedText(unformattedText: String?) {

    this.settings?.apply {
//                val fontSize = baseActivity.resources.getDimensionPixelOffset(R.dimen.textField_10);
        setDomStorageEnabled(true);
        setJavaScriptEnabled(true);
        setCacheMode(WebSettings.LOAD_NO_CACHE);

    }
    val unfText =
        if (unformattedText.isNullOrEmpty()) context.getString(R.string.enter_description) else unformattedText
    var fontName = ThemeUtils.getFontName(SelfLearningApplication.fontId)


    val takeAways = "<html>\n" +
            "<head>\n" +
            "    <style>\n" +
            "        @font-face {\n" +
            "            font-family: '${fontName.first}';\n" +
            "            src: url('font/${
                fontName.second

            }');\n" +
            "        }\n" +
            "        #font {\n" +
            "            font-family: '${fontName.first}';\n" +
            "        }\n" +
            "body {\n" +
            "    font-family: ${fontName.first};\n" +
            "font-size: 14px;\n" +
            "color: #262626;" +
            "}" +

            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            unfText?.trim()?.replace("<p><br></p>", "\n") +
            "</body></html>"

    this.loadDataWithBaseURL(
        "file:///android_res/",
        takeAways ?: "",
        "text/html",
        "utf-8",
        "about:blank"
    );

}

@BindingAdapter(value = ["unFormattedString"])
fun TextView.setHtmlString(data: String?) {
    if (data.isNullOrEmpty()) {
        text = ""
    } else {
        text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(data, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
        } else {
            Html.fromHtml(data)
        }
    }
}

@BindingAdapter(value = ["htmlString", "count"], requireAll = false)
fun TextView.setLimitedText(data: String?, lines: Int? = 9) {
    if (data.isNullOrEmpty()) {
        text = ""
    } else {
        setSpanString(SpannableString(data))
//text=data
        var textCount = lineCount
        if (textCount > lines ?: 9) {
            textCount = lines ?: 9


            val linesList: ArrayList<CharSequence> = ArrayList()

            for (i in 0 until textCount) {
                val start = getLayout().getLineStart(i)
                val end = getLayout().getLineEnd(i)
                val substring: CharSequence = getText().subSequence(start, end)
                linesList.add(substring.toString())
            }
            linesList.add("...")
            text = linesList.joinToString(" ")
        }
    }


}

fun EditText.setWordLimit(limit: Int = 100, onTextChanged: () -> Unit = {}) {


    doOnTextChanged { input, _, _, _ ->
        if (!input.isNullOrEmpty()) {
            val list = input.toString().trim().split("\\s+".toRegex())
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
            showLog("ON_TEXT_CHANGE", "list size >> ${list.size} ... limit >> ${limit}")
            if (list.size > limit) {
                setText(list.dropLast(list.size - limit).joinToString(" ").toString())
                setSelection(text.length)
            }
//            val newText= data?.substring(0,count)

        }
        onTextChanged()
    }


}


@BindingAdapter("complexityLevel")
fun TextView.setComplexityLevel(type: Int?) {
    try {


        if (type.isNullOrZero()) {
            setTextColor(ContextCompat.getColor(context, context.getAttrColor(R.attr.colorPrimary)))
            backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        context.getAttrColor(R.attr.levelBgColor)
                    )
                )
        } else {
            when (type) {
                ComplexityLevel.ADVANCED -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            context.getAttrColor(R.attr.accentColor_Red)
                        )
                    )
                    backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                context.getAttrColor(R.attr.levelBgColor)
                            )
                        )
                }
                ComplexityLevel.BEGINNER -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            context.getAttrColor(R.attr.accentColor_Green)
                        )
                    )
                    backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                context.getAttrColor(R.attr.levelBgColor)
                            )
                        )

                }
                ComplexityLevel.INTERMEDIATE -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            context.getAttrColor(R.attr.colorPrimary)
                        )
                    )
                    backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                context.getAttrColor(R.attr.levelBgColor)
                            )
                        )

                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@BindingAdapter("limitColor")
fun TextView.setLimitColor(isLimitExceed: Boolean) {
    if (!isLimitExceed) {
        setTextColor(context.getAttrResource(R.attr.accentColor_Red))
    } else {
        setTextColor(context.getAttrResource(R.attr.blackTextColor))

    }
}
