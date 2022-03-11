package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource

class OTPVerifyViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var otp: String = ""

    fun otpVerify() {
//        if (otp.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_otp)))
//        } else if (otp.length < 4) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_valid_otp)))
//
//        } else {
        updateResponseObserver(Resource.Success(null, ""))
//        }
    }

}