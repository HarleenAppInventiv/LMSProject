package com.selflearningcoursecreationapp.ui.authentication.otp_verify

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.OtpData
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OtpType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OTPVerifyViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var otpData = OtpData()
    var argBundle: OTPVerifyFragmentArgs? = null
    var token: String? = null


    fun otpVerify() {
        val errorId = otpData.isValid()
        if (errorId == 0) {
            valOTP(argBundle, token ?: "")
        } else {
            updateResponseObserver(Resource.Error(ToastData(errorId)))
        }
    }


    fun reqOTP() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (argBundle?.email.isNullOrEmpty()) {
                map["phone"] = argBundle?.phone.toString()
                map["countryCode"] = argBundle?.countryCode ?: ""

            } else {
                map["email"] = argBundle?.email.toString()

            }
            map["OtpType"] = argBundle?.type.toString()
            map["unit"] = 60


            val response = when (argBundle?.type) {
                OtpType.TYPE_EMAIL -> {
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

    private fun valOTP(args: OTPVerifyFragmentArgs?, token: String) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (args?.email.isNullOrEmpty()) {
                map["phone"] = args?.phone.toString()
                map["countryCode"] = args?.countryCode ?: ""

            } else {
                map["email"] = args?.email.toString()
            }
            map["otpValue"] = otpData.otp ?: ""
            map["otpType"] = args?.type.toString()
            map["unit"] = 60
            map["fcmDeviceToken"] = token
            map["viMode"] = SelfLearningApplication.isViOn ?: false

            updateResponseObserver(Resource.Loading())

            val response = when (args?.type) {
                OtpType.TYPE_EMAIL -> {
                    repo.verEmailOtp(map)
                }
                else -> {
                    repo.verOtp(map)
                }
            }
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*> && (args?.type != OtpType.TYPE_FORGOT && args?.type != OtpType.TYPE_EMAIL)) {
                        val data = it.value as BaseResponse<UserResponse>
                        saveUserDataInDB(data)
                        saveViMode(data.resource?.user?.viMode ?: false)
                        delay(2000)
                    }

                    updateResponseObserver(it)
                }
            }

        }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_OTP_VAL, ApiEndPoints.API_VERIFY_EMAIL -> {
                otpVerify()
            }
            ApiEndPoints.API_ADD_EMAIL, ApiEndPoints.API_OTP_REQ -> {
                reqOTP()
            }

        }
    }

}