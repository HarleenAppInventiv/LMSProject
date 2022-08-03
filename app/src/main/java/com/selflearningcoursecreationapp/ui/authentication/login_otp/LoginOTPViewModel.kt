package com.selflearningcoursecreationapp.ui.authentication.login_otp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.ValidationConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginOTPViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var phone = MutableLiveData<String>().apply {
        value = ""
    }

    var countryCode = ""
    var cc = ""


    fun loginViaOTP() {

        when {
            phone.value.isNullOrEmpty() -> {
                updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_phone_number)))

            }
            phone.value!!.length < ValidationConst.MIN_NO_LENGTH -> {
                updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_phone_number)))
            }
            else -> {
                reqOTP()

            }
        }


    }


    private fun reqOTP() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()


            map["phone"] = phone.value!!
            map["OtpType"] = "${OtpType.TYPE_LOGIN}"
            map["countryCode"] = countryCode


            val response = repo.reqOtp(map)
            withContext(Dispatchers.IO) {
                phone
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ -> {
                loginViaOTP()
            }
        }
    }

}



