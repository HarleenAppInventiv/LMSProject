package com.selflearningcoursecreationapp.ui.authentication.login_otp

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData

class LoginOTPViewModel(private val repo: LoginOTPRepo) : BaseViewModel() {
    var phone: String = ""


    fun loginViaOTP() {

        if (phone.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_phone_number)))

        } else if (phone.length < 10) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_phone_number)))
        } else {
            updateResponseObserver(Resource.Success(null, "101"))

        }


    }



}
