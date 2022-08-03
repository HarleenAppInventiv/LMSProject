package com.selflearningcoursecreationapp.extensions

import android.text.TextUtils
import androidx.core.util.PatternsCompat
import com.selflearningcoursecreationapp.utils.ValidationConst

fun String.isValidEmail(): Boolean {
    return try {
        !TextUtils.isEmpty(this) && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
    } catch (ex: NullPointerException) {
        false
    }
}

fun String.isPasswordValid(): Boolean {
    return isNotEmpty() && length >= ValidationConst.MIN_PASSWORD_LENGTH
//    val expression = ("^(?=.*[0-9])"
//            + "(?=.*[a-z])(?=.*[A-Z])"
//            + "(?=.*[@#$%^&+=!*])"
//            + "(?=\\S+$).{6,40}$")
//
//    val pattern = Pattern.compile(expression)
//    val matcher = pattern.matcher(this)
//    return matcher.matches()
}