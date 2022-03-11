package com.selflearningcoursecreationapp.ui.authentication.forgotPass

import com.selflearningcoursecreationapp.base.BaseViewModel

class ForgotPassViewModel(private val repo: ForgotPassRepo) : BaseViewModel() {

//    var phone_email = ""
//
//    fun forgotPass() {
//
//        if (phone_email.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_phone_email)))
//        }
//        if (phone_email.isDigitsOnly()) {
//            if (phone_email.length < 10) {
//                updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_phone_number)))
//            }else {
//                updateResponseObserver(Resource.Success(null, ""))
//            }
//        }
//        if (!phone_email.isValidEmail()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_email)))
//
//        } else {
//            updateResponseObserver(Resource.Success(null, ""))
//        }
//
//
//    }
}