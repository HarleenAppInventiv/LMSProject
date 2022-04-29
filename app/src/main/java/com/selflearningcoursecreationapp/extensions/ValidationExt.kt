package com.selflearningcoursecreationapp.extensions

import android.text.TextUtils
import androidx.core.util.PatternsCompat

fun String.isValidEmail(): Boolean {
    try {
        return !TextUtils.isEmpty(this) && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
    } catch (ex: NullPointerException) {
        return false
    }
}

fun String.isPasswordValid(): Boolean {
    return isNotEmpty() && length >= 8
//    val expression = ("^(?=.*[0-9])"
//            + "(?=.*[a-z])(?=.*[A-Z])"
//            + "(?=.*[@#$%^&+=!*])"
//            + "(?=\\S+$).{6,40}$")
//
//    val pattern = Pattern.compile(expression)
//    val matcher = pattern.matcher(this)
//    return matcher.matches()
}