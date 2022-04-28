package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.OtpData
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OTPVerifyViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var otpData = OtpData()

    fun otpVerify(argBundle: OTPVerifyFragmentArgs?) {
        val errorId = otpData.isValid()
        if (errorId == 0) {
            valOTP(argBundle)
        } else {
            updateResponseObserver(Resource.Error(ToastData(errorId)))
        }
    }


    fun reqOTP(argBundle: OTPVerifyFragmentArgs?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (argBundle?.email.isNullOrEmpty()) {
                map["phone"] = argBundle?.phone.toString()
                map["countryCode"] = argBundle?.countryCode ?: ""

            } else {
                map["email"] = argBundle?.email.toString()
            }
            map["OtpType"] = argBundle?.type.toString()


            var response = when (argBundle?.type) {
                OTP_TYPE.TYPE_EMAIL -> {
                    repo.reqEmailOtp(map)
                }
                else -> {
                    repo.reqOtp(map)
                }
            }
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }

    fun valOTP(args: OTPVerifyFragmentArgs?) = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        if (args?.email.isNullOrEmpty()) {
            map["phone"] = args?.phone.toString()
            map["countryCode"] = args?.countryCode ?: ""

        } else {
            map["email"] = args?.email.toString()
        }
        map["otpValue"] = otpData.otp ?: ""
        map["otpType"] = args?.type.toString()

        updateResponseObserver(Resource.Loading())

        var response = when (args?.type) {
            OTP_TYPE.TYPE_EMAIL -> {
                repo.verEmailOtp(map)
            }
            else -> {
                repo.verOtp(map)
            }
        }
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*> && (args?.type != OTP_TYPE.TYPE_FORGOT && args?.type != OTP_TYPE.TYPE_EMAIL)) {
//                    if (args?.type != OTP_TYPE.TYPE_FORGOT) {
                    val data = it.value as BaseResponse<UserResponse>
                    saveUserDataInDB(data)
                    delay(2000)
//                    }
                }

                updateResponseObserver(it)
            }
        }

    }

}