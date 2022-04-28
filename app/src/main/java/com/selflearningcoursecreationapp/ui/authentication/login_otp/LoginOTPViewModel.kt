package com.selflearningcoursecreationapp.ui.authentication.login_otp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyRepo
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginOTPViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var phone = MutableLiveData<String>().apply {
        value = ""
    }

    var cc = ""

    fun loginViaOTP(selectedCountryCodeWithPlus: String) {

        if (phone.value.isNullOrEmpty()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_phone_number)))

        } else if (phone.value!!.length < 5) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_phone_number)))
        } else {
            reqOTP(selectedCountryCodeWithPlus)

        }


    }


    fun reqOTP(selectedCountryCodeWithPlus: String) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()


            map["phone"] = phone.value!!
//            map["unit"] = "60"
            map["OtpType"] = "${OTP_TYPE.TYPE_LOGIN}"
            map["countryCode"] = selectedCountryCodeWithPlus


            var response = repo.reqOtp(map)
            withContext(Dispatchers.IO) {
                phone
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }


}
