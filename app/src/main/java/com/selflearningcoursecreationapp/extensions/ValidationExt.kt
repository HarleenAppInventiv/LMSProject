package com.selflearningcoursecreationapp.extensions

import android.text.TextUtils
import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

fun String.isValidEmail(): Boolean {
    try {
        return !TextUtils.isEmpty(this) && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
    } catch (ex: NullPointerException) {
        return false
    }
}

fun String.isPasswordValid(): Boolean {
    val expression = ("^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,40}$")

    val pattern = Pattern.compile(expression)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}