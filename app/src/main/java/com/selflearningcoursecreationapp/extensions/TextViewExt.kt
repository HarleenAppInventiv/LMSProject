package com.selflearningcoursecreationapp.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.selflearningcoursecreationapp.R

fun TextView.content(): String = text.toString().trim()



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
                R.drawable.ic_password_show)

            else -> ContextCompat.getDrawable(context, R.drawable.ic_password_hide)
        }

        setCompoundDrawablesRelativeWithIntrinsicBounds(
            compoundDrawables.get(0),
            compoundDrawables.get(1),
            drawableRight,
            compoundDrawables.get(3)
        )
    }


    setOnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP && compoundDrawables[2] != null && motionEvent.rawX >= this.right - (compoundDrawables.get(
                2).bounds.width() + paddingRight)
        )
            if (transformationMethod is PasswordTransformationMethod) {
                transformationMethod = HideReturnsTransformationMethod()
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    compoundDrawables.get(0),
                    compoundDrawables.get(1),
                    ContextCompat.getDrawable(context, R.drawable.ic_password_hide),
                    compoundDrawables.get(3)
                )
            } else {
                transformationMethod = PasswordTransformationMethod()
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    compoundDrawables.get(0),
                    compoundDrawables.get(1),
                    ContextCompat.getDrawable(context, R.drawable.ic_password_show),
                    compoundDrawables.get(3)
                )

            }
        return@setOnTouchListener false
    }
}