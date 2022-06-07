package com.selflearningcoursecreationapp.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doOnTextChanged
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


fun TextView.content(): String = text.toString().trim()
fun TextView.isBlank(): Boolean = text.toString().trim().isNullOrEmpty()


fun TextView.showKeyBoard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInputFromWindow(applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
}


@SuppressLint("ClickableViewAccessibility")
fun TextView.showHidePassword() {


    doOnTextChanged { text, _, _, _ ->
        val drawableRight = when {
            text.isNullOrEmpty() -> null
            transformationMethod is PasswordTransformationMethod -> ContextCompat.getDrawable(
                context,
                R.drawable.ic_password_hide
            )

            else -> ContextCompat.getDrawable(context, R.drawable.ic_password_show)
        }

        setCompoundDrawablesRelativeWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.ic_key_password),
            compoundDrawables.get(1),
            drawableRight,
            compoundDrawables.get(3)
        )
        compoundDrawablesRelative[0]?.let {

            it.colorFilter =
                PorterDuffColorFilter(
                    ThemeUtils.getAppColor(context),
                    PorterDuff.Mode.SRC_IN
                )
        }
    }


    setOnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP && compoundDrawables[2] != null && motionEvent.rawX >= this.right - (compoundDrawables.get(
                2
            ).bounds.width() + paddingRight)
        )
            if (transformationMethod is PasswordTransformationMethod) {
                transformationMethod = HideReturnsTransformationMethod()
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_key_password),
                    compoundDrawables.get(1),
                    ContextCompat.getDrawable(context, R.drawable.ic_password_show),
                    compoundDrawables.get(3)
                )
            } else {
                transformationMethod = PasswordTransformationMethod()
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.ic_key_password),
                    compoundDrawables.get(1),
                    ContextCompat.getDrawable(context, R.drawable.ic_password_hide),
                    compoundDrawables.get(3)
                )

            }
        compoundDrawablesRelative[0]?.let {

            it.colorFilter =
                PorterDuffColorFilter(
                    ThemeUtils.getAppColor(context),
                    PorterDuff.Mode.SRC_IN
                )
        }
        return@setOnTouchListener false
    }

    setNoSpaceFilter()
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.onRightDrawableClick(onClick: () -> Unit) {

    setOnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP && compoundDrawables[2] != null && motionEvent.rawX >= this.right - (compoundDrawables.get(
                2
            ).bounds.width() + paddingRight)
        )
            onClick()
        return@setOnTouchListener false
    }
}

fun EditText.otpHelper() {
    setOnKeyListener { _, keyCode, keyEvent ->
        showLog("OTP", "setOnKeyListener>> >> $keyEvent")


        when {
            keyCode == KeyEvent.KEYCODE_DEL && isBlank() -> {


                val view = focusSearch(View.FOCUS_LEFT)
                view?.requestFocus()
                if (view is EditText)
                    view.setSelection(view.text.toString().length)


            }
            keyEvent.action == KeyEvent.ACTION_UP && content().length == 1 && keyCode != KeyEvent.KEYCODE_DEL -> {

                //            if (content().length == 1) {
                setOverridingText(keyEvent)
            }
        }


//        }

        false
    }


    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            showLog("OTP", "beforeTextChanged>>" + p0.toString())
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            showLog("OTP", p0.toString())
            showLog("OTP", "p1>>> $p1")
            showLog("OTP", "p2>>> $p2")
            showLog("OTP", "p3>>> $p3")
            if (p0.toString().length == 1 && p3 == 1) {

                val view = focusSearch(View.FOCUS_RIGHT)
                view?.let { it.requestFocus() } ?: kotlin.run {
                    this@otpHelper.hideKeyboard()
                }
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            Log.d("text", "afterTextChanged: ")
        }

    })
}


fun EditText.textHelper(isLast: Boolean) {
    if (isLast) {
        imeOptions = EditorInfo.IME_ACTION_DONE
    }
    setOnEditorActionListener { textView, i, keyEvent ->

        if (i == EditorInfo.IME_ACTION_NEXT) {
            val view = focusSearch(View.FOCUS_LEFT)
            view?.let {
                requestFocus()
                if (view is EditText) {
                    view.setSelection(view.text.toString().length)
                }
            } ?: kotlin.run {
                hideKeyboard()
            }
        } else if (i == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard()
        }

        return@setOnEditorActionListener false
    }
}

private fun EditText.setOverridingText(keyEvent: KeyEvent) {
    val m = keyEvent.getUnicodeChar(keyEvent.metaState).toChar().toString()
    if (m.isDigitsOnly()) {
        showLog("OTP", "charrr>> >> ${m}")
        val view = focusSearch(View.FOCUS_RIGHT)

        view?.let {

            if (view is EditText && view.text.toString().isNullOrEmpty()) {

                view.setText(m)
                view.requestFocus()
                view.setSelection(view.text.toString().length)

            } else {
                setText(m)
                it.requestFocus()

            }
        } ?: kotlin.run {
            this.hideKeyboard()
        }
    }
}

fun TextView.setNoSpaceFilter() {
    filters = filters.let {
        it + InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { char -> char.isWhitespace() }
        }
    }


}


fun TextView.setTextResizable(fullText: String?) {
    if (fullText.isNullOrEmpty()) {
        text = ""
    } else if (fullText.length <= 100) {
        text = fullText
    } else {
        setLessText(fullText)

    }
}

fun TextView.setLessText(fullText: String) {
    var lessText = "${fullText.subSequence(0, 100)}  ${context.getString(R.string.read_more)}"
    val spanText = SpanUtils.with(context, lessText).startPos(102)
        .textColor(ThemeUtils.getAppColor(context))
        .isUnderline()
        .isBold().getCallback {
            setFullText(fullText)
        }.getSpanString()

    setSpanString(spanText)
}

fun TextView.setFullText(fullText: String) {
    var completeText = "${fullText}  ${context.getString(R.string.read_less)}"
    val spanText = SpanUtils.with(context, completeText).startPos(fullText.length + 2)
        .textColor(ThemeUtils.getAppColor(context))
        .isUnderline()
        .isBold().getCallback {
            setLessText(fullText)
        }.getSpanString()
    setSpanString(spanText)
}


fun EditText.addDecimalLimiter(maxLimit: Int = 2, beforeDecimal: Int = 4) {

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            var str = this@addDecimalLimiter.text!!.toString()
            if (str.isEmpty()) return

            val str2 = decimalLimiter(str, maxLimit, beforeDecimal)



            if (str2 != str) {
                this@addDecimalLimiter.setText(str2)
                val pos = this@addDecimalLimiter.text!!.length
                this@addDecimalLimiter.setSelection(pos)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}

fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int, BEFORE_DECIMAL: Int): String {

    var str = string
    if (str[0] == '.') str = "0$str"
    val max = str.length

    var rFinal = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char

    val decimalCount = str.count { ".".contains(it) }

    if (decimalCount > 1)
        return str.dropLast(1)
    if (!str.contains(".") && str.length > 4) {
        return str.dropLast(str.length - 4)

    } else {
        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
    }
    return rFinal
}


fun LMSTextView.setTextSelected(isSelected: Boolean) {
    if (isSelected) {
        changeTextColor(ThemeConstants.TYPE_BLACK)
        changeFontType(ThemeConstants.FONT_MEDIUM)
    } else {
        changeTextColor(ThemeConstants.TYPE_BODY)
        changeFontType(ThemeConstants.FONT_REGULAR)
    }
}

fun LMSTextView.setStepColor(isSelected: Boolean) {
    if (isSelected) {
        changeTextColor(ThemeConstants.TYPE_WHITE)
        changeFontType(ThemeConstants.FONT_SEMI_BOLD)
        changeBackgroundTint(ThemeConstants.TYPE_THEME)
    } else {
        changeTextColor(ThemeConstants.TYPE_BODY)
        changeFontType(ThemeConstants.FONT_REGULAR)
        changeBackgroundTint(ThemeConstants.TYPE_BODY)
    }
}